# Adressdatenprüfer

Kleines Java-Programm, das eine CSV-Datei mit Adressen einliest und auf Fehler prüft. Gefundene Probleme werden mit der Adress-ID und einer kurzen Beschreibung auf der Konsole ausgegeben.

## Geprüft wird

- **Fehlende Werte:** Felder, die leer sind oder nur Leerzeichen enthalten
- **Postleitzahl:** Sie muss aus genau fünf Ziffern bestehen
- **Format:** Leerzeichen am Anfang oder Ende eines Feldes sowie Kleinschreibung am Anfang von Straße und Ort
- **Dubletten:** Exakte Dubletten und ähnliche Dubletten, die sich nur in der Schreibweise unterscheiden (Groß-/Kleinschreibung, Leerzeichen am Rand, "Straße", "Strasse" oder "Str.")

Bei Dubletten wird immer der spätere Eintrag gemeldet.

## Eingabedatei

Die Datei `data/adressen.csv` wird mit UTF-8 eingelesen. Als Trennzeichen dient ein Semikolon, die erste Zeile ist die Kopfzeile:

```
id;strasse;hausnummer;plz;ort
1;Hauptstraße;12;46236;Bottrop
2;Hauptstr.;12;46236;Bottrop
3;Hauptstraße;12;46236;Bottrop
4;bahnhofstraße;5;4623;Bottrop
5;Lindenweg;;45127;Essen
```

Hat eine Zeile nicht genau fünf Spalten, bricht das Programm mit einer Fehlermeldung ab.

## Ausgabe

```
5 Adressen geprueft, 5 Befunde:
id 2 | DUBLETTE_AEHNLICH | Aehnliche Dublette von id 1
id 3 | DUBLETTE_EXAKT | Exakte Dublette von id 1
id 4 | UNGUELTIGE_PLZ | PLZ '4623' ist keine 5-stellige Zahl
id 4 | FORMATFEHLER | Feld 'strasse' beginnt mit Kleinbuchstaben
id 5 | FEHLENDER_WERT | Feld 'hausnummer' ist leer
```

Die Befunde sind nach ID sortiert.

## Voraussetzungen

- Java 21
- Maven