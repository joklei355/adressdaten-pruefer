package de.jonas.adressen;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException {
        List<Adresse> adressen = AdressLeser.lese(Path.of("data", "adressen.csv"));
        System.out.println(adressen.size() + " Adressen eingelesen");
        adressen.stream().limit(3).forEach(System.out::println);
    }
}