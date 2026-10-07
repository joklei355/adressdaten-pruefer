package de.jonas.adressen;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AdressLeser {
	/**
	 * Liest die CSV-Datei Zeile für Zeile ein und wandelt jede Zeile in eine Adresse um.
	 * Die Kopfzeile wird übersprungen. Werte werden nicht verändert,
	 * damit der Prüfer die Fehler später selbst findet.
	 */
    public static List<Adresse> lese(Path datei) throws IOException {
        List<String> zeilen = Files.readAllLines(datei, StandardCharsets.UTF_8);
        List<Adresse> adressen = new ArrayList<>();

        // Index 0 ist die Kopfzeile, deshalb Start bei 1
        for (int i = 1; i < zeilen.size(); i++) {
            String zeile = zeilen.get(i);
            if (zeile.isBlank()) {
                continue;
            }
            // -1 sorgt dafür, dass auch leere Felder am Ende erhalten bleiben
            String[] teile = zeile.split(";", -1);
            if (teile.length != 5) {
                throw new IllegalArgumentException("Zeile " + (i + 1) + " hat "
                        + teile.length + " statt 5 Spalten: " + zeile);
            }
            adressen.add(new Adresse(Integer.parseInt(teile[0]),
                    teile[1], teile[2], teile[3], teile[4]));
        }
        return adressen;
    }
}