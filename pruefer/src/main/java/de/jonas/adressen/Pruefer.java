package de.jonas.adressen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Pruefer {

    public static List<Befund> pruefe(List<Adresse> adressen) {
        List<Befund> befunde = new ArrayList<>();
        Map<String, Adresse> gesehen = new HashMap<>();

        for (Adresse a : adressen) {
            pruefeFehlendeWerte(a, befunde);
            pruefePlz(a, befunde);
            pruefeFormat(a, befunde);
            pruefeDublette(a, gesehen, befunde);
        }
        befunde.sort(Comparator.comparingInt(Befund::id));
        return befunde;
    }

    private static Map<String, String> felder(Adresse a) {
        Map<String, String> felder = new LinkedHashMap<>();
        felder.put("strasse", a.strasse());
        felder.put("hausnummer", a.hausnummer());
        felder.put("plz", a.plz());
        felder.put("ort", a.ort());
        return felder;
    }

    private static void pruefeFehlendeWerte(Adresse a, List<Befund> befunde) {
        for (var feld : felder(a).entrySet()) {
            if (feld.getValue().isBlank()) {
                befunde.add(new Befund(a.id(), Fehlerart.FEHLENDER_WERT,
                        "Feld '" + feld.getKey() + "' ist leer"));
            }
        }
    }

    private static void pruefePlz(Adresse a, List<Befund> befunde) {
        String plz = a.plz().trim();
        if (!plz.isEmpty() && !plz.matches("\\d{5}")) {
            befunde.add(new Befund(a.id(), Fehlerart.UNGUELTIGE_PLZ,
                    "PLZ '" + a.plz() + "' ist keine 5-stellige Zahl"));
        }
    }

    private static void pruefeFormat(Adresse a, List<Befund> befunde) {
        for (var feld : felder(a).entrySet()) {
            String name = feld.getKey();
            String wert = feld.getValue();
            if (wert.isBlank()) {
                continue; // leere Felder meldet schon pruefeFehlendeWerte
            }
            if (!wert.equals(wert.trim())) {
                befunde.add(new Befund(a.id(), Fehlerart.FORMATFEHLER,
                        "Feld '" + name + "' hat Leerzeichen am Anfang oder Ende"));
            }
            boolean istText = name.equals("strasse") || name.equals("ort");
            if (istText && Character.isLowerCase(wert.trim().charAt(0))) {
                befunde.add(new Befund(a.id(), Fehlerart.FORMATFEHLER,
                        "Feld '" + name + "' beginnt mit Kleinbuchstaben"));
            }
        }
    }

    private static void pruefeDublette(Adresse a, Map<String, Adresse> gesehen,
                                       List<Befund> befunde) {
        String schluessel = normalisiere(a);
        Adresse erste = gesehen.get(schluessel);
        if (erste == null) {
            gesehen.put(schluessel, a);
        } else if (rohSchluessel(a).equals(rohSchluessel(erste))) {
            befunde.add(new Befund(a.id(), Fehlerart.DUBLETTE_EXAKT,
                    "Exakte Dublette von id " + erste.id()));
        } else {
            befunde.add(new Befund(a.id(), Fehlerart.DUBLETTE_AEHNLICH,
                    "Aehnliche Dublette von id " + erste.id()));
        }
    }

    private static String rohSchluessel(Adresse a) {
        return a.strasse() + "|" + a.hausnummer() + "|" + a.plz() + "|" + a.ort();
    }

    // Vereinheitlicht Schreibweisen, damit "Hauptstr." und "Hauptstrasse" gleich werden
    private static String normalisiere(Adresse a) {
        String strasse = a.strasse().trim().toLowerCase(Locale.GERMAN)
                .replace("\u00df", "ss")
                .replaceAll("(strasse|str\\.?)$", "str");
        return strasse + "|" + a.hausnummer().trim().toLowerCase(Locale.GERMAN)
                + "|" + a.plz().trim() + "|" + a.ort().trim().toLowerCase(Locale.GERMAN);
    }
}