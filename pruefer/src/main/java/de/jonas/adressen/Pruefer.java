package de.jonas.adressen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Pruefer {
	/**
	 * Geht alle Adressen einmal durch und sammelt die gefundenen Fehler.
	 * Einzelprüfungen schauen nur auf eine Adresse, die Dublettenprüfung
	 * vergleicht über die Map "gesehen" mit den bisherigen Adressen.
	 * Am Ende werden die Befunde nach id sortiert.
	 */
    public static List<Befund> pruefe(List<Adresse> adressen) {
        List<Befund> befunde = new ArrayList<>(); 
        Map<String, Adresse> gesehen = new HashMap<>(); // merkt sich alle adressen die wir schon durchlaufen haben

        for (Adresse a : adressen) {   
            pruefeFehlendeWerte(a, befunde);
            pruefePlz(a, befunde);
            pruefeFormat(a, befunde);
            pruefeDublette(a, gesehen, befunde);
        }
        befunde.sort(Comparator.comparingInt(Befund::id));
        return befunde;
    } 
    /**
     * Packt die vier Textfelder einer Adresse in eine Map (Name -> Wert),
     * damit ich in den Prüfungen per Schleife über alle Felder gehen kann.
     */
    private static Map<String, String> felder(Adresse a) {
        Map<String, String> felder = new LinkedHashMap<>();
        felder.put("strasse", a.strasse());
        felder.put("hausnummer", a.hausnummer());
        felder.put("plz", a.plz());
        felder.put("ort", a.ort());
        return felder;
    }
    /** Meldet jedes Feld, das leer ist oder nur Leerzeichen enthält. */
    private static void pruefeFehlendeWerte(Adresse a, List<Befund> befunde) {
        for (var feld : felder(a).entrySet()) {
            if (feld.getValue().isBlank()) {
                befunde.add(new Befund(a.id(), Fehlerart.FEHLENDER_WERT,
                        "Feld '" + feld.getKey() + "' ist leer"));
            }
        }
    }
    /**
     * Prüft, ob die PLZ aus genau 5 Ziffern besteht (regulärer Ausdruck).
     * Leere PLZ werden hier übersprungen, die meldet schon pruefeFehlendeWerte.
     */
    private static void pruefePlz(Adresse a, List<Befund> befunde) {
        String plz = a.plz().trim();
        if (!plz.isEmpty() && !plz.matches("\\d{5}")) {
            befunde.add(new Befund(a.id(), Fehlerart.UNGUELTIGE_PLZ,
                    "PLZ '" + a.plz() + "' ist keine 5-stellige Zahl"));
        }
    }
    /**
     * Sucht Formatfehler: Leerzeichen am Anfang oder Ende eines Feldes und
     * Kleinschreibung am Anfang von Straße und Ort. Leere Felder werden übersprungen.
     */
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
    /**
     * Erkennt Dubletten. Die Adresse wird normalisiert und in der Map gesucht.
     * Gibt es sie schon, ist es eine Dublette: "exakt", wenn die Rohwerte
     * gleich sind, sonst "ähnlich" (nur andere Schreibweise).
     * Gemeldet wird immer der spätere Eintrag.
     */
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
    /** Setzt die Felder unverändert zu einem Vergleichstext zusammen (für exakte Dubletten). */
    private static String rohSchluessel(Adresse a) {
        return a.strasse() + "|" + a.hausnummer() + "|" + a.plz() + "|" + a.ort();
    }

    /**
     * Vereinheitlicht die Schreibweise: kleingeschrieben, ohne Leerzeichen am Rand,
     * "ß" wird zu "ss", und "straße", "strasse", "str." am Ende werden zu "str".
     * So werden "Hauptstr." und "Hauptstraße" als gleiche Adresse erkannt.
     */
    private static String normalisiere(Adresse a) {
        String strasse = a.strasse().trim().toLowerCase(Locale.GERMAN)
                .replace("\u00df", "ss")
                .replaceAll("(strasse|str\\.?)$", "str");
        return strasse + "|" + a.hausnummer().trim().toLowerCase(Locale.GERMAN)
                + "|" + a.plz().trim() + "|" + a.ort().trim().toLowerCase(Locale.GERMAN);
    }
}