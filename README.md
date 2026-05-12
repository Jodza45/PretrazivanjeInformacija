# 19241_Jovan_Milosevic_lab1

Laboratorijska vezba 1 iz predmeta Pretrazivanje informacija.

## Sta projekat radi

- Indeksira 4 tekstualna fajla iz `data/original`.
- Svaki fajl predstavlja jedan Lucene dokument.
- Dokument ima polja:
  - `contents` - sadrzaj fajla, `TextField`
  - `filename` - naziv fajla, `StringField`
  - `fullpath` - kompletna putanja, `StringField`
  - `sizeBytes` - velicina fajla u bajtovima, `LongPoint`
  - `sizeBytesStored` - sacuvana velicina za prikaz rezultata, `StoredField`
- Deli svaki originalni fajl na 100 delova i pravi kolekciju od 400 fajlova u `data/split`.
- Kreira dva indeksa:
  - `indexes/original`
  - `indexes/split`
- Poredi vreme kreiranja i velicinu oba indeksa.
- Izvrsava Boolean upit sa AND, OR i NOT nad oba indeksa.
- Izvrsava `PointRangeQuery`, jer je poslednja cifra broja indeksa `1`.
- Oba upita se prave na dva nacina: direktno kao Lucene objektni model i iz tekstualnog oblika.

## Pokretanje

Iz foldera projekta pokrenuti:

```powershell
.\run.ps1
```

Ako se pokrece rucno:

```powershell
javac -encoding UTF-8 -cp "lib/*" -d bin (Get-ChildItem -Recurse -Filter *.java src\main\java,src\test\java).FullName
java -cp "bin;lib/*" lab1.Lab1App
```

## Tekstualni fajlovi

Korisceni su Project Gutenberg fajlovi:

- `alice_in_wonderland.txt`
- `the_time_machine.txt`
- `jekyll_and_hyde.txt`
- `frankenstein.txt`

Svi su obicni `.txt` fajlovi i velicine su izmedju 30KB i 500KB.

## Napomena za PointRangeQuery

Standardni `QueryParser` ne pravi automatski `LongPoint` upit za numericko polje. Zato metoda
`QueryFactory.createPointRangeQueryParsed` koristi `StandardQueryParser` i `PointsConfig`, isto kao
u primerima sa vezbi, da bi tekst oblika `sizeBytes:[100 TO 500000]` bio parsiran kao numericki
`LongPoint` opseg.
