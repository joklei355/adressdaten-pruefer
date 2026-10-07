package de.jonas.adressen;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException {
        List<Adresse> adressen = AdressLeser.lese(Path.of("data", "adressen.csv"));
        List<Befund> befunde = Pruefer.pruefe(adressen);

        System.out.println(adressen.size() + " Adressen geprueft, "
                + befunde.size() + " Befunde:");
        for (Befund b : befunde) {
            System.out.println("id " + b.id() + " | " + b.art() + " | " + b.beschreibung());
        }
    }
}