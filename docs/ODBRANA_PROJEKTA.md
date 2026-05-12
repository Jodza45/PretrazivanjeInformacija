# Odbrana projekta - Laboratorijska vežba 1

Student: Jovan Milošević  
Broj indeksa: 19241  
Naziv projekta: `19241_Jovan_Milosevic_lab1`  
Predmet: Pretraživanje informacija

Ovaj dokument služi kao detaljno objašnjenje projekta za odbranu. Ideja je da možeš da ga koristiš kao skriptu dok profesoru pokazuješ kod, strukturu foldera i rezultate pokretanja.

---

## 1. Šta je bio zadatak

Zadatak laboratorijske vežbe je bio da se pomoću Apache Lucene biblioteke:

1. napravi Java aplikacija,
2. pronađu najmanje 4 obična tekstualna fajla veličine od 30KB do 500KB,
3. nad tim fajlovima kreira Lucene indeks,
4. svaki fajl predstavi kao poseban dokument u indeksu,
5. dokumenti indeksiraju sa poljima: sadržaj fajla, naziv fajla, kompletna putanja i veličina fajla u bajtovima,
6. od originalnih fajlova napravi nova kolekcija od 400 fajlova tako što se svaki originalni fajl deli na 100 delova,
7. kreira drugi indeks nad podeljenom kolekcijom,
8. uporedi veličina oba indeksa i vreme potrebno za kreiranje,
9. napravi logički upit koji koristi `AND`, `OR` i `NOT`,
10. isti logički upit napravi direktno kao objektni model i parsiranjem tekstualnog upita,
11. napravi dodatni upit čiji tip zavisi od poslednje cifre broja indeksa,
12. oba upita izvrši nad oba indeksa.

Pošto je broj indeksa `19241`, poslednja cifra je `1`, pa dodatni upit mora da bude `PointRangeQuery`.

---

## 2. Kako se projekat pokreće

Projekat može da se pokrene iz Eclipse-a ili iz PowerShell-a.

### 2.1. Pokretanje iz Eclipse-a

U Eclipse-u:

1. `File` -> `Import`
2. `General` -> `Existing Projects into Workspace`
3. izabrati folder projekta:

```text
C:\Users\jovan\jovan\Desktop\bitno\4. godina\pretrazivanje_informacija\lab1\19241_Jovan_Milosevic_lab1
```

4. kliknuti `Finish`,
5. otvoriti klasu `src/main/java/lab1/Lab1App.java`,
6. desni klik na fajl,
7. `Run As` -> `Java Application`.

Projekat ima `.project` i `.classpath` fajlove, pa Eclipse vidi Java projekat i Lucene `.jar` biblioteke iz `lib` foldera.

### 2.2. Pokretanje iz PowerShell-a

Iz PowerShell-a:

```powershell
cd "C:\Users\jovan\jovan\Desktop\bitno\4. godina\pretrazivanje_informacija\lab1\19241_Jovan_Milosevic_lab1"
powershell -ExecutionPolicy Bypass -File .\run.ps1
```

Skripta `run.ps1` radi tri stvari:

1. kompajlira sve Java fajlove,
2. pokreće malu proveru `Lab1Checks`,
3. pokreće glavnu aplikaciju `Lab1App`.

Ako se program pokreće direktno iz Eclipse-a, `run.ps1` nije neophodan.

---

## 3. Struktura projekta

Najvažniji folderi i fajlovi su:

```text
19241_Jovan_Milosevic_lab1
├── data
│   ├── original
│   └── split
├── docs
│   ├── izvestaj.md
│   └── ODBRANA_PROJEKTA.md
├── indexes
│   ├── original
│   └── split
├── lib
├── src
│   ├── main/java/lab1
│   └── test/java/lab1
├── .classpath
├── .project
├── pom.xml
├── README.md
└── run.ps1
```

### 3.1. `data/original`

U ovom folderu se nalaze 4 originalna tekstualna fajla. To su fajlovi nad kojima se pravi prvi indeks.

Korišćeni fajlovi su:

| Fajl | Veličina |
| --- | ---: |
| `alice_in_wonderland.txt` | 174314 B |
| `frankenstein.txt` | 448888 B |
| `jekyll_and_hyde.txt` | 163489 B |
| `the_time_machine.txt` | 204378 B |

Svi fajlovi su obični `.txt` fajlovi i svaki je između 30KB i 500KB, što je traženo zadatkom.

### 3.2. `data/split`

Ovaj folder se dobija automatski. Program uzima svaki fajl iz `data/original` i deli ga na 100 približno jednakih delova.

Pošto ima 4 originalna fajla:

```text
4 fajla * 100 delova = 400 fajlova
```

Zato se pri pokretanju ispisuje:

```text
Kreirano delova: 400
```

### 3.3. `indexes/original`

U ovom folderu se čuva Lucene indeks kreiran nad originalnom kolekcijom od 4 fajla.

Svaki originalni fajl je jedan Lucene dokument, pa ovaj indeks ima 4 dokumenta.

### 3.4. `indexes/split`

U ovom folderu se čuva Lucene indeks kreiran nad podeljenom kolekcijom od 400 fajlova.

Svaki mali deo fajla je jedan Lucene dokument, pa ovaj indeks ima 400 dokumenata.

### 3.5. `lib`

U folderu `lib` su Lucene biblioteke:

```text
lucene-core-9.12.2.jar
lucene-analysis-common-9.12.2.jar
lucene-queryparser-9.12.2.jar
lucene-queries-9.12.2.jar
```

Najvažnije:

- `lucene-core` sadrži osnovne klase za indeksiranje i pretragu,
- `lucene-analysis-common` sadrži analizatore kao što je `StandardAnalyzer`,
- `lucene-queryparser` sadrži `QueryParser`,
- `lucene-queries` sadrži dodatne klase za upite.

---

## 4. Glavni tok programa

Glavni program je klasa:

```text
src/main/java/lab1/Lab1App.java
```

Ona predstavlja ulaznu tačku aplikacije. U njoj se nalazi `main` metoda.

Program radi sledećim redom:

1. određuje putanje do foldera:
   - `data/original`,
   - `data/split`,
   - `indexes/original`,
   - `indexes/split`;
2. proverava da u `data/original` postoje najmanje 4 `.txt` fajla;
3. deli originalne fajlove na 400 delova;
4. pravi prvi indeks nad originalnim fajlovima;
5. pravi drugi indeks nad podeljenim fajlovima;
6. ispisuje vreme kreiranja i veličinu oba indeksa;
7. kreira Boolean upit direktno;
8. kreira isti Boolean upit parsiranjem tekstualnog upita;
9. kreira `PointRangeQuery` direktno;
10. kreira isti `PointRangeQuery` iz tekstualnog oblika;
11. izvršava sva četiri upita nad oba indeksa;
12. ispisuje broj pogodaka, vreme pretrage i osnovne podatke o rezultatima.

Važan deo iz `Lab1App`:

```java
Query booleanDirect = QueryFactory.createBooleanQueryDirect();
Query booleanParsed = QueryFactory.createBooleanQueryParsed(analyzer);
long lowerBytes = 100L;
long upperBytes = 500_000L;
Query pointDirect = QueryFactory.createPointRangeQueryDirect(lowerBytes, upperBytes);
Query pointParsed = QueryFactory.createPointRangeQueryParsed(analyzer,
        "sizeBytes:[" + lowerBytes + " TO " + upperBytes + "]");
```

Ovim se jasno vidi da se oba tipa upita prave na dva načina:

- direktno kroz Java/Lucene klase,
- preko tekstualnog oblika.

---

## 5. Objašnjenje klasa

### 5.1. `Lab1App`

`Lab1App` je glavna klasa projekta. Ona ne radi svu logiku sama, nego poziva pomoćne klase.

Njena uloga je da poveže ceo proces:

- proveri ulazne fajlove,
- pokrene deljenje fajlova,
- pokrene indeksiranje,
- pokrene pretragu,
- ispiše rezultate.

To je dobro zato što kod nije sav u jednoj ogromnoj klasi. Svaka pomoćna klasa ima svoju odgovornost.

### 5.2. `FileSplitter`

`FileSplitter` služi za deljenje originalne kolekcije.

Ima dve bitne metode:

```java
splitCollection(Path sourceDirectory, Path targetDirectory, int partsPerFile)
splitFile(Path sourceFile, Path targetDirectory, int numberOfParts)
```

`splitCollection`:

- uzima sve `.txt` fajlove iz `data/original`,
- za svaki fajl poziva `splitFile`,
- pravi ukupno 400 fajlova u `data/split`.

`splitFile`:

- učita tekst fajla kao string,
- izračuna približnu veličinu jednog dela,
- pravi 100 fajlova,
- ne vodi računa o kraju rečenice ili pasusa, što je dozvoljeno zadatkom.

Formula koja se koristi za veličinu jednog dela:

```java
int partSize = Math.max(1, (int) Math.ceil(length / (double) numberOfParts));
```

To znači: dužina teksta se podeli sa brojem delova, zaokruži se naviše, i tako se dobija približna veličina jednog dela.

Primer naziva generisanih fajlova:

```text
alice_in_wonderland_part_001.txt
alice_in_wonderland_part_002.txt
...
alice_in_wonderland_part_100.txt
```

### 5.3. `Indexer`

`Indexer` je klasa koja pravi Lucene indeks.

Glavna metoda:

```java
createIndex(Path dataDirectory, Path indexDirectory, Analyzer analyzer)
```

Ona:

1. obriše stari indeks ako postoji,
2. pronađe sve `.txt` fajlove u datom folderu,
3. otvori Lucene direktorijum preko `FSDirectory`,
4. napravi `IndexWriterConfig`,
5. napravi `IndexWriter`,
6. svaki tekstualni fajl pretvori u Lucene `Document`,
7. doda dokument u indeks,
8. zatvori writer,
9. izmeri vreme indeksiranja,
10. izračuna veličinu indeksa na fajl sistemu.

Najvažniji Lucene objekti:

```java
Directory directory = FSDirectory.open(indexDirectory);
IndexWriterConfig config = new IndexWriterConfig(analyzer);
IndexWriter writer = new IndexWriter(directory, config);
```

Objašnjenje:

- `FSDirectory` znači da se indeks čuva na disku,
- `IndexWriterConfig` sadrži konfiguraciju indeksiranja,
- `IndexWriter` je klasa koja upisuje dokumente u indeks.

### 5.4. `LuceneFields`

Ova klasa sadrži konstante za nazive polja u indeksu:

```java
public static final String CONTENTS = "contents";
public static final String FILENAME = "filename";
public static final String FULL_PATH = "fullpath";
public static final String SIZE_BYTES = "sizeBytes";
public static final String SIZE_BYTES_STORED = "sizeBytesStored";
```

Ovo je urađeno da se isti stringovi ne bi ponavljali ručno kroz više klasa. Na primer, ako se polje zove `contents`, bolje je koristiti konstantu nego svuda pisati `"contents"`.

### 5.5. `QueryFactory`

`QueryFactory` je klasa za kreiranje upita.

Ona pravi:

- Boolean upit direktno,
- Boolean upit preko `QueryParser`,
- `PointRangeQuery` direktno,
- `PointRangeQuery` iz tekstualnog oblika.

Ova klasa je posebno važna za odbranu, jer se u zadatku traži da se isti upit kreira na dva načina.

### 5.6. `Searcher`

`Searcher` izvršava upit nad indeksom.

Glavna metoda:

```java
search(Path indexDirectory, String queryName, Query query, int maxResults)
```

Ona:

1. otvara indeks preko `FSDirectory`,
2. otvara `DirectoryReader`,
3. pravi `IndexSearcher`,
4. izvršava upit,
5. meri vreme pretrage,
6. čita sačuvana polja iz pronađenih dokumenata,
7. vraća `SearchReport`.

Najvažniji deo:

```java
TopDocs topDocs = searcher.search(query, maxResults);
```

`TopDocs` sadrži rezultate koje je Lucene pronašao za zadati upit. U ovom projektu koristimo ga samo da prođemo kroz pronađene dokumente i prikažemo osnovne podatke.

### 5.7. `FileUtils`

`FileUtils` sadrži pomoćne metode za rad sa fajlovima i folderima:

- `recreateDirectory` - obriše folder ako postoji i napravi ga ponovo,
- `deleteRecursively` - briše folder i sve u njemu,
- `directorySize` - računa ukupnu veličinu svih fajlova u folderu.

Ovo se koristi da se pri svakom pokretanju dobije čist indeks i čista podeljena kolekcija.

### 5.8. `IndexReport`, `SearchReport`, `SearchHit`

Ovo su `record` klase. One služe kao jednostavni kontejneri za podatke.

`IndexReport` čuva:

- folder podataka,
- folder indeksa,
- broj dokumenata,
- vreme kreiranja indeksa,
- veličinu indeksa.

`SearchReport` čuva:

- naziv upita,
- folder indeksa,
- Lucene oblik upita,
- ukupan broj pogodaka,
- vreme pretrage,
- listu prikazanih rezultata.

`SearchHit` čuva:

- naziv fajla,
- punu putanju,
- veličinu fajla.

---

## 6. Kako je predstavljen dokument u indeksu

U Lucene-u je dokument predstavljen klasom:

```java
Document
```

U ovom projektu svaki tekstualni fajl postaje jedan `Document`.

Metoda koja pravi dokument nalazi se u `Indexer` klasi:

```java
private static Document createDocument(Path file) throws IOException
```

Za svaki fajl se dodaju sledeća polja:

```java
document.add(new TextField(LuceneFields.CONTENTS, contents, Field.Store.NO));
document.add(new StringField(LuceneFields.FILENAME, file.getFileName().toString(), Field.Store.YES));
document.add(new StringField(LuceneFields.FULL_PATH, file.toAbsolutePath().toString(), Field.Store.YES));
document.add(new LongPoint(LuceneFields.SIZE_BYTES, size));
document.add(new StoredField(LuceneFields.SIZE_BYTES_STORED, size));
```

### 6.1. `contents`

```java
new TextField(LuceneFields.CONTENTS, contents, Field.Store.NO)
```

Ovo polje sadrži tekst fajla.

Koristi se `TextField` zato što:

- tekst treba analizirati,
- tekst treba tokenizovati,
- nad tekstom treba raditi pretragu po terminima.

`Field.Store.NO` znači da se ceo sadržaj fajla ne čuva u indeksu za kasnije prikazivanje. Sadržaj se indeksira da bi mogao da se pretražuje, ali se ne vraća kao sačuvana vrednost.

To je dobro zato što bi čuvanje celog teksta u indeksu nepotrebno povećalo indeks.

### 6.2. `filename`

```java
new StringField(LuceneFields.FILENAME, file.getFileName().toString(), Field.Store.YES)
```

Ovo polje čuva naziv fajla.

Koristi se `StringField` zato što naziv fajla ne treba tokenizovati kao običan tekst. Vrednost se čuva kao celina.

`Field.Store.YES` znači da naziv fajla možemo da pročitamo iz rezultata pretrage i prikažemo na konzoli.

### 6.3. `fullpath`

```java
new StringField(LuceneFields.FULL_PATH, file.toAbsolutePath().toString(), Field.Store.YES)
```

Ovo polje čuva kompletnu putanju do fajla.

I ovo je `StringField`, jer putanja treba da se čuva kao jedna vrednost. Čuva se u indeksu da bi se mogla prikazati ili koristiti za pronalaženje originalnog fajla.

### 6.4. `sizeBytes`

```java
new LongPoint(LuceneFields.SIZE_BYTES, size)
```

Ovo polje čuva veličinu fajla u bajtovima kao numeričku vrednost.

Koristi se `LongPoint` zato što je u Lucene-u za numeričke range upite potrebno koristiti point polja.

Ovo polje je bitno zbog `PointRangeQuery` upita.

### 6.5. `sizeBytesStored`

```java
new StoredField(LuceneFields.SIZE_BYTES_STORED, size)
```

`LongPoint` služi za pretragu, ali se njegova vrednost ne može jednostavno pročitati iz rezultata kao stored vrednost. Zato postoji dodatno polje `sizeBytesStored`.

Ono služi samo da se veličina fajla prikaže u rezultatima.

---

## 7. Analyzer

U projektu se koristi:

```java
StandardAnalyzer analyzer = new StandardAnalyzer()
```

Analyzer je Lucene komponenta koja obrađuje tekst pre indeksiranja i pre parsiranja upita.

`StandardAnalyzer` radi stvari kao što su:

- deli tekst na tokene,
- pretvara slova u mala slova,
- uklanja neke česte reči,
- pravi termine koji ulaze u indeks.

Važno je da se isti analyzer koristi i pri indeksiranju i pri parsiranju upita.

U projektu:

- `Indexer` koristi `StandardAnalyzer` za indeksiranje,
- `QueryFactory.createBooleanQueryParsed` koristi isti analyzer u `QueryParser`.

To je važno jer termin koji se traži mora da bude obrađen na isti način kao termin koji je upisan u indeks.

Primer:

```text
Life
life
LIFE
```

Posle analize se svodi na isti termin:

```text
life
```

---

## 8. Prvi indeks i drugi indeks

Zadatak traži dva indeksa.

### 8.1. Prvi indeks

Prvi indeks se pravi nad originalnom kolekcijom:

```text
data/original
```

Tu postoje 4 tekstualna fajla. Svaki fajl postaje jedan Lucene dokument.

Zato program ispisuje:

```text
Broj dokumenata: 4
```

### 8.2. Drugi indeks

Drugi indeks se pravi nad podeljenom kolekcijom:

```text
data/split
```

Tu postoji 400 tekstualnih fajlova. Svaki fajl postaje jedan Lucene dokument.

Zato program ispisuje:

```text
Broj dokumenata: 400
```

### 8.3. Zašto se veličina indeksa razlikuje

Originalna kolekcija i podeljena kolekcija imaju približno isti tekstualni sadržaj, ali druga kolekcija ima mnogo više dokumenata.

Drugi indeks može da bude veći zbog dodatnih informacija koje Lucene čuva po dokumentu, kao što su:

- metadata za svaki dokument,
- stored polja,
- strukture invertovanog indeksa,
- informacije o segmentima.

Zato nije čudno da indeks od 400 dokumenata bude veći od indeksa od 4 dokumenta, iako je tekstualni sadržaj skoro isti.

---

## 9. Boolean upit

Zadatak traži logički upit od najmanje 3 termina koji koristi sve 3 logičke operacije:

- `AND`,
- `OR`,
- `NOT`.

U projektu je izabran upit:

```text
((life AND time) OR man) AND NOT queen
```

Termini su:

- `life`,
- `time`,
- `man`,
- `queen`.

Operacije su:

- `life AND time` koristi `AND`,
- `(life AND time) OR man` koristi `OR`,
- `AND NOT queen` koristi `NOT`.

Značenje upita:

```text
Pronađi dokumente koji sadrže oba termina life i time,
ili sadrže termin man,
ali ne smeju da sadrže termin queen.
```

### 9.1. Direktno kreiranje Boolean upita

Direktno kreiranje se radi u metodi:

```java
QueryFactory.createBooleanQueryDirect()
```

Prvo se naprave pojedinačni `TermQuery` upiti:

```java
Query life = new TermQuery(new Term(LuceneFields.CONTENTS, "life"));
Query time = new TermQuery(new Term(LuceneFields.CONTENTS, "time"));
Query man = new TermQuery(new Term(LuceneFields.CONTENTS, "man"));
Query queen = new TermQuery(new Term(LuceneFields.CONTENTS, "queen"));
```

`TermQuery` je osnovni upit koji traži jedan termin u određenom polju.

Zatim se pravi deo:

```java
BooleanQuery lifeAndTime = new BooleanQuery.Builder()
        .add(life, BooleanClause.Occur.MUST)
        .add(time, BooleanClause.Occur.MUST)
        .build();
```

`MUST` znači da dokument mora da zadovolji taj podupit. Dva `MUST` uslova zajedno predstavljaju `AND`.

Zatim se pravi ceo Boolean upit:

```java
return new BooleanQuery.Builder()
        .add(lifeAndTime, BooleanClause.Occur.SHOULD)
        .add(man, BooleanClause.Occur.SHOULD)
        .add(queen, BooleanClause.Occur.MUST_NOT)
        .build();
```

Ovde:

- `SHOULD` predstavlja `OR`,
- `MUST_NOT` predstavlja `NOT`.

### 9.2. Kreiranje Boolean upita preko parsera

Parser verzija je u metodi:

```java
QueryFactory.createBooleanQueryParsed(Analyzer analyzer)
```

Kod:

```java
QueryParser parser = new QueryParser(LuceneFields.CONTENTS, analyzer);
return parser.parse("((life AND time) OR man) AND NOT queen");
```

Ovde se tekstualni upit parsira i pretvara u Lucene `Query` objekat.

To je drugi način koji je zadatak tražio.

### 9.3. Zašto direktni i parsirani upit imaju malo drugačiji ispis

U rezultatu se vidi da direktni i parsirani upit imaju malo drugačiji `toString()` oblik:

```text
(+contents:life +contents:time) contents:man -contents:queen
```

i:

```text
+((+contents:life +contents:time) contents:man) -contents:queen
```

To ne znači da je jedan pogrešan. Lucene interno prikazuje strukturu upita. Bitno je da oba upita predstavljaju istu ideju i daju isti broj pogodaka.

---

## 10. PointRangeQuery

Pošto se broj indeksa završava cifrom `1`, prema zadatku dodatni upit mora da bude:

```text
PointRangeQuery
```

`PointRangeQuery` se koristi za pretragu numeričkih vrednosti u zadatom opsegu.

U ovom projektu se pretražuje polje:

```text
sizeBytes
```

To je veličina fajla u bajtovima.

Izabran je opseg:

```text
100 do 500000 bajtova
```

Tekstualni oblik upita:

```text
sizeBytes:[100 TO 500000]
```

Značenje:

```text
Pronađi dokumente čija je veličina fajla između 100 i 500000 bajtova.
```

Pošto su originalni fajlovi između 30KB i 500KB, ovaj upit vraća sva 4 originalna dokumenta. Pošto su podeljeni fajlovi manji od 500KB, vraća i svih 400 podeljenih dokumenata.

### 10.1. Direktno kreiranje PointRangeQuery upita

Direktna verzija:

```java
public static Query createPointRangeQueryDirect(long lowerBytes, long upperBytes) {
    return LongPoint.newRangeQuery(LuceneFields.SIZE_BYTES, lowerBytes, upperBytes);
}
```

Ovde se koristi:

```java
LongPoint.newRangeQuery(...)
```

To je Lucene factory metoda za numerički range upit nad `long` vrednostima.

### 10.2. Kreiranje PointRangeQuery upita iz tekstualnog oblika

Klasični `QueryParser` odlično radi za tekstualne upite, ali ne zna automatski da od stringa:

```text
sizeBytes:[100 TO 500000]
```

napravi `LongPoint` upit za numeričko point polje.

Zato se u projektu, kao i u primerima sa vežbi, koristi `StandardQueryParser` sa `PointsConfig` mapom. Metoda je:

```java
createPointRangeQueryParsed(Analyzer analyzer, String text)
```

Ona:

1. primi tekstualni oblik upita,
2. napravi `StandardQueryParser`,
3. u `PointsConfig` mapu doda informaciju da je `sizeBytes` numeričko `Long` polje,
4. prosledi mapu parseru,
5. parsira tekstualni upit u Lucene `PointRangeQuery`.

Kod izgleda ovako:

```java
StandardQueryParser parser = new StandardQueryParser(analyzer);
Map<String, PointsConfig> pointsConfig = new HashMap<>();
pointsConfig.put(LuceneFields.SIZE_BYTES, new PointsConfig(NumberFormat.getInstance(), Long.class));
parser.setPointsConfigMap(pointsConfig);
return parser.parse(text, LuceneFields.SIZE_BYTES);
```

To ispunjava zahtev zadatka: postoji tekstualni oblik upita, parser ga obrađuje, i iz njega se dobija isti tip objektog modela upita kao kod direktne verzije.

Ovo je važno objasniti profesoru ako pita zašto nije korišćen običan `QueryParser` za `PointRangeQuery`.

Kratak odgovor:

```text
QueryParser je korišćen za Boolean tekstualni upit. Za PointRangeQuery nad LongPoint poljem koristim StandardQueryParser i PointsConfig, zato što parser mora da zna da je sizeBytes numeričko Long polje.
```

---

## 11. Pretraga i rezultati

Pretraga se radi u klasi:

```text
Searcher.java
```

Postupak:

```java
FSDirectory directory = FSDirectory.open(indexDirectory);
DirectoryReader reader = DirectoryReader.open(directory);
IndexSearcher searcher = new IndexSearcher(reader);
TopDocs topDocs = searcher.search(query, maxResults);
```

Objašnjenje:

- `FSDirectory` otvara indeks koji je sačuvan na disku,
- `DirectoryReader` omogućava čitanje indeksa,
- `IndexSearcher` izvršava upit,
- `TopDocs` sadrži pronađene rezultate.

Za svaki rezultat se čitaju stored polja:

```java
Document document = searcher.storedFields().document(scoreDoc.doc);
```

Zatim se ispisuju:

- veličina fajla,
- naziv fajla.

Primer ispisa:

```text
size=163489 file=jekyll_and_hyde.txt
```

---

## 12. Očekivani rezultati pri pokretanju

Pri pokretanju se prvo vidi:

```text
Kreirano delova: 400
```

Zatim za originalni indeks:

```text
Broj dokumenata: 4
```

Za podeljeni indeks:

```text
Broj dokumenata: 400
```

Program ispisuje i:

- vreme kreiranja originalnog indeksa,
- veličinu originalnog indeksa,
- vreme kreiranja podeljenog indeksa,
- veličinu podeljenog indeksa.

Vremena mogu da se razlikuju od pokretanja do pokretanja. To je normalno, jer zavise od računara, diska, Jave i trenutnog opterećenja sistema.

### 12.1. Primer rezultata

Jedno pokretanje je dalo:

| Indeks | Broj dokumenata | Vreme kreiranja | Veličina indeksa |
| --- | ---: | ---: | ---: |
| Originalna kolekcija | 4 | 696 ms | 409868 B |
| Podeljena kolekcija | 400 | 1289 ms | 481673 B |

Rezultati upita:

| Upit | Indeks | Broj pogodaka |
| --- | --- | ---: |
| Boolean direktno | Originalni | 2 |
| Boolean direktno | Podeljeni | 175 |
| Boolean parser | Originalni | 2 |
| Boolean parser | Podeljeni | 175 |
| PointRange direktno | Originalni | 4 |
| PointRange direktno | Podeljeni | 400 |
| PointRange parser | Originalni | 4 |
| PointRange parser | Podeljeni | 400 |

Ovi brojevi pokazuju da su upiti stvarno izvršeni nad oba indeksa.

---

## 13. Poruke koje Lucene ispisuje pri pokretanju

Pri pokretanju se mogu videti poruke:

```text
INFO: Using MemorySegmentIndexInput with Java 21 or later
WARNING: Java vector incubator module is not readable
```

To nisu greške u programu.

To su informativne poruke iz Lucene biblioteke i Java runtime-a.

Značenje:

- Lucene je prepoznao da se koristi Java 21 ili novija.
- Lucene javlja da može da koristi neke dodatne optimizacije ako se uključi Java Vector API modul.

Program radi normalno i bez toga. Ako bi se želelo uklanjanje warning poruke, moglo bi da se doda VM argument:

```text
--add-modules jdk.incubator.vector
```

Ali za laboratorijsku vežbu to nije potrebno.

---

## 14. Šta pokazati profesoru

Predlog redosleda za odbranu:

1. Pokazati `data/original` i reći da sadrži 4 tekstualna fajla iz Project Gutenberg kolekcije.
2. Pokazati `data/split` i reći da program generiše 400 fajlova, po 100 delova od svakog originalnog fajla.
3. Pokazati `Lab1App.java` kao glavnu klasu.
4. Pokazati `FileSplitter.java` i objasniti kako se fajlovi dele.
5. Pokazati `Indexer.java` i objasniti kako se pravi Lucene `Document`.
6. Posebno pokazati polja:
   - `TextField` za sadržaj,
   - `StringField` za naziv i putanju,
   - `LongPoint` za veličinu,
   - `StoredField` za prikaz veličine.
7. Pokazati `QueryFactory.java`.
8. Objasniti Boolean upit:
   - direktno,
   - preko `QueryParser`.
9. Objasniti `PointRangeQuery`:
   - zato što je poslednja cifra broja indeksa `1`,
   - pretraga po veličini fajla u bajtovima.
10. Pokrenuti program i pokazati:
    - `Kreirano delova: 400`,
    - originalni indeks ima 4 dokumenta,
    - podeljeni indeks ima 400 dokumenata,
    - vreme i veličinu oba indeksa,
    - rezultate oba upita nad oba indeksa.

---

## 15. Kratko objašnjenje za usmenu odbranu

Ako treba ukratko da objasniš projekat, možeš reći:

```text
Projekat koristi Apache Lucene za indeksiranje i pretraživanje tekstualnih fajlova.
Prvo se indeksira kolekcija od 4 originalna tekstualna fajla. Svaki fajl je jedan Lucene Document.
Dokument ima polja contents, filename, fullpath i sizeBytes.
Zatim se svaki originalni fajl deli na 100 delova, čime se dobija nova kolekcija od 400 fajlova.
Nad tom kolekcijom se pravi drugi indeks.
Program meri vreme kreiranja i veličinu oba indeksa.
Zatim se izvršava Boolean upit sa AND, OR i NOT, napravljen direktno i preko QueryParser-a.
Pošto se moj broj indeksa završava na 1, dodatni upit je PointRangeQuery nad numeričkim poljem sizeBytes.
I taj upit je napravljen direktno i iz tekstualnog oblika, pa izvršen nad oba indeksa.
```

---

## 16. Moguća pitanja i odgovori

### Pitanje: Šta je Lucene dokument u ovom projektu?

Odgovor:

```text
Jedan tekstualni fajl je jedan Lucene Document. U originalnom indeksu ima 4 dokumenta, a u podeljenom indeksu 400 dokumenata.
```

### Pitanje: Zašto je `contents` `TextField`?

Odgovor:

```text
Zato što sadržaj fajla treba analizirati i tokenizovati da bi mogao da se pretražuje po terminima.
```

### Pitanje: Zašto su `filename` i `fullpath` `StringField`?

Odgovor:

```text
Zato što su to vrednosti koje želim da čuvam kao celinu, bez tokenizacije. One se čuvaju u indeksu da bih mogao da ih prikažem u rezultatima.
```

### Pitanje: Zašto postoje i `sizeBytes` i `sizeBytesStored`?

Odgovor:

```text
sizeBytes je LongPoint i koristi se za numeričku pretragu preko PointRangeQuery. sizeBytesStored je StoredField i koristi se da se veličina fajla pročita i prikaže u rezultatima.
```

### Pitanje: Šta radi `StandardAnalyzer`?

Odgovor:

```text
StandardAnalyzer obrađuje tekst: deli ga na tokene, normalizuje ga i pravi termine koji se upisuju u indeks. Isti analyzer se koristi i pri parsiranju tekstualnog upita.
```

### Pitanje: Koja je razlika između direktnog upita i parser upita?

Odgovor:

```text
Direktni upit pravim ručno pomoću Lucene klasa kao što su TermQuery, BooleanQuery i LongPoint. Parser upit kreće od tekstualnog zapisa i pretvara ga u Query objekat.
```

### Pitanje: Zašto je dodatni upit `PointRangeQuery`?

Odgovor:

```text
Zato što zadatak kaže da se tip dodatnog upita bira po poslednjoj cifri broja indeksa. Moj broj indeksa je 19241, poslednja cifra je 1, a za 1 se radi PointRangeQuery.
```

### Pitanje: Šta tačno radi `PointRangeQuery` u projektu?

Odgovor:

```text
Traži dokumente čija je veličina fajla u bajtovima između 100 i 500000. Pretraga se radi nad numeričkim poljem sizeBytes.
```

### Pitanje: Zašto je drugi indeks veći?

Odgovor:

```text
Zato što druga kolekcija ima 400 dokumenata, a Lucene za svaki dokument čuva dodatne strukture i metadata. Iako je tekstualni sadržaj sličan, broj dokumenata je mnogo veći.
```

### Pitanje: Zašto se vremena indeksiranja menjaju?

Odgovor:

```text
Vreme zavisi od trenutnog opterećenja računara, diska i Java runtime-a. Bitno je da program meri vreme za oba indeksa pri svakom pokretanju.
```

---

## 17. Veza sa zahtevima iz zadatka

| Zahtev iz zadatka | Gde je urađeno |
| --- | --- |
| Java aplikacija sa nazivom projekta | Folder `19241_Jovan_Milosevic_lab1` |
| Najmanje 4 tekstualna fajla | `data/original` |
| Fajlovi veličine 30KB do 500KB | 4 Project Gutenberg `.txt` fajla |
| Indeks nad originalnim fajlovima | `indexes/original`, klasa `Indexer` |
| Svaki fajl poseban dokument | `Indexer.createDocument` se poziva za svaki fajl |
| Polje za sadržaj | `contents`, `TextField` |
| Polje za naziv fajla | `filename`, `StringField` |
| Polje za kompletnu putanju | `fullpath`, `StringField` |
| Polje za veličinu fajla | `sizeBytes`, `LongPoint` |
| Nova kolekcija od 400 fajlova | `FileSplitter`, folder `data/split` |
| Drugi indeks | `indexes/split` |
| Poređenje veličine i vremena | `IndexReport`, ispis u `Lab1App` |
| Boolean upit sa AND/OR/NOT | `QueryFactory.createBooleanQueryDirect` i `createBooleanQueryParsed` |
| Upit direktno i parserom | `QueryFactory` |
| Dodatni upit po poslednjoj cifri indeksa | `PointRangeQuery`, jer je poslednja cifra `1` |
| Izvršavanje nad oba indeksa | `runQueryOnBothIndexes` u `Lab1App` |

---

## 18. Zaključak

Projekat demonstrira osnovni tok rada sa Apache Lucene bibliotekom:

1. priprema tekstualnih podataka,
2. kreiranje dokumenata,
3. indeksiranje,
4. čuvanje indeksa na fajl sistemu,
5. pretraživanje indeksa,
6. poređenje ponašanja na manjoj i većoj kolekciji dokumenata.

Najvažnije za odbranu je da pokažeš da razumeš razliku između:

- originalne i podeljene kolekcije,
- dokumenta i polja u Lucene-u,
- `TextField`, `StringField`, `LongPoint` i `StoredField`,
- direktnog kreiranja upita i kreiranja preko parsera,
- tekstualnog Boolean upita i numeričkog `PointRangeQuery` upita.
