# Detaljno objašnjenje klasa i koncepata (Skripta za odbranu)

Ovaj dokument sadrži pojednostavljena, ali tehnički precizna objašnjenja svake klase u projektu, kao i ključnih koncepata Apache Lucene biblioteke koji se često pitaju na usmenoj odbrani.

---

## 1. `Lab1App.java` (Glavna klasa - "Dirigent")

Ova klasa je ulazna tačka programa. Ona ne sadrži složenu logiku obrade, već diriguje ostalim klasama po tačno utvrđenom redosledu:
1. **Putanja i validacija:** Definiše putanje do foldera i proverava da li u `data/original` ima bar 4 tekstualna fajla.
2. **Deljenje fajlova:** Poziva `FileSplitter` da isecka originalne fajlove na 400 manjih.
3. **Inicijalizacija Analyzer-a:** Pravi JEDAN `StandardAnalyzer` koji se prosleđuje svuda. Ovo je ključno kako bi se tekst na isti način obrađivao i prilikom upisivanja u indeks, i prilikom pretrage (npr. pretvaranje u mala slova).
4. **Indeksiranje:** Poziva `Indexer` da napravi dva indeksa (originalni od 4 fajla i podeljeni od 400 fajlova).
5. **Priprema upita:** Poziva `QueryFactory` da pripremi Boolean i PointRange upite (kroz kod i parsiranjem).
6. **Pretraga:** Pomoću pomoćne metode `runQueryOnBothIndexes` poziva `Searcher` da izvrši isti upit i nad malim i nad velikim indeksom, radi poređenja rezultata i vremena.

---

## 2. `Indexer.java` (Kreator Invertovanog indeksa)

Ova klasa uzima `.txt` fajlove sa diska i pretvara ih u Lucene Indeks (strukturu optimizovanu za pretragu).
* **`FSDirectory.open(...)`**: Govori Lucene-u da indeks sačuva fizički na hard disku (umesto u RAM memoriji).
* **`IndexWriterConfig`**: Konfiguracija koja govori Writer-u da koristi naš `StandardAnalyzer` i da uvek kreira indeks od nule (`OpenMode.CREATE`).
* **`IndexWriter`**: Glavna Lucene klasa koja jedina ima pravo da upisuje podatke u indeks.

### Kreiranje Dokumenta (`createDocument`)
U Lucene-u, svaki fajl mora da se pretvori u objekat `Document`. Zatim mu se dodaju polja (`Fields`):
* **Sadržaj (`TextField`)**: Koristi se za dugačak tekst jer `TextField` prolazi kroz Analyzer (secka se na reči). 
* **Ime i putanja (`StringField`)**: Ne prolazi kroz Analyzer. Pamti se tačno onakvo kakvo jeste (u komadu), jer pretražujemo ili vraćamo celu putanju, a ne pojedinačna slova iz nje.
* **Veličina (`LongPoint`)**: Numeričko polje u vidu BKD stabla. Služi ISKLJUČIVO za munjevito brzu pretragu brojeva (od-do).
* **Veličina za ispis (`StoredField`)**: Pošto se vrednost iz `LongPoint`-a ne može lako isčitati nazad za prikaz, dodajemo paralelno i `StoredField` iz koga čitamo veličinu kada želimo da je ispišemo u konzoli.

### 💡 Važno: `Field.Store.YES` vs `Field.Store.NO`
* **`Field.Store.NO` (kod Sadržaja)**: Znači da reči idu u invertovani indeks za pretragu, ali se dugačak originalni tekst **ne čuva** u magacinu. Tako štedimo ogromnu količinu memorije.
* **`Field.Store.YES` (kod Imena fajla)**: Znači da se podatak indeksira, ali se i **čuva u originalu**, tako da možemo da ga izvučemo i ispišemo korisniku kao rezultat (npr. ime pronađenog fajla).

---

## 3. `QueryFactory.java` (Fabrika upita)

Zadatak traži da se upiti kreiraju na dva načina: "direktno" (ručno pisanjem Java koda) i "parsiranjem" (od običnog teksta).

### Logički (Boolean) upit: `((life AND time) OR man) AND NOT queen`
* **Direktno kreiranje**: Koriste se `TermQuery` objekti za reči, i pakuju se u `BooleanQuery`. Pošto u Lucene-u ne postoje reči AND, OR, NOT, koriste se takozvane `Occur` klauzule:
  * `MUST` = AND (dokument mora ovo da sadrži)
  * `SHOULD` = OR (bilo bi dobro da sadrži, spaja više uslova)
  * `MUST_NOT` = NOT (dokument ovo ne sme da sadrži)
* **Parsirano kreiranje**: Koristi se klasičan `QueryParser` kome se da string upita, a on sam napravi gorepomenuti `BooleanQuery`.

### Numerički (PointRange) upit: `sizeBytes:[100 TO 500000]`
Ovaj upit je dodat jer se indeks studenta završava na broj 1.
* **Direktno kreiranje**: Pravi se jednostavnim pozivom fabričke metode `LongPoint.newRangeQuery(...)`.
* **Parsirano kreiranje (Trik pitanje!)**: 
  * Ne može da se koristi običan `QueryParser` jer on 100 i 500000 vidi kao "slova" a ne brojeve.
  * Zato se koristi moderniji **`StandardQueryParser`**.
  * Pravi se **`PointsConfig`** mapa kojom se parseru izričito kaže: *"Polje sizeBytes je numeričkog tipa Long"*. Tek tada parser zna da string pretvori u ispravan matematički `PointRangeQuery`.

---

## 4. `Searcher.java` (Pretraživač)

Klasa koja izvršava pripremljene upite nad indeksom.
* **`DirectoryReader`**: Za razliku od Writer-a, Reader samo čita indeks i optimizovan je za brzinu.
* **`IndexSearcher`**: Njemu se prosleđuje upit, a on vraća objekat `TopDocs`.
* **`TopDocs` i `ScoreDoc`**:
  * `TopDocs` sadrži broj ukupnih pogodaka i listu `ScoreDoc` objekata.
  * `ScoreDoc` nije sam fajl, već samo interni broj (ID) dokumenta u Lucene-u.
* Da bismo ispisali rezultate, uzimamo taj ID i tražimo od Lucene-a da nam učita **sačuvana polja** za taj dokument: `searcher.storedFields().document(scoreDoc.doc)`.
* Zatim iz tog dokumenta izvlačimo polja koja imaju `Store.YES` (poput `FILENAME` i `SIZE_BYTES_STORED`) i pakujemo ih za ispis.

---

## 5. Pomoćne klase

* **`FileSplitter.java`**: Čita originalne fajlove u celosti (kao String) i matematički ih deli na po 100 jednakih delova (po broju karaktera), snimajući ih kao nove male fajlove.
* **`LuceneFields.java`**: Služi samo za definisanje konstanti (`public static final String`). Štiti nas od grešaka u kucanju jer umesto stringova `"contents"` ili `"filename"` koristimo nazive konstanti.
* **`FileUtils.java`**: Manipulacija fajl sistemom (brisanje starih indeksa i foldera pre početka novog rada).
* **`IndexReport`, `SearchReport`, `SearchHit`**: Java `record` klase (takozvani DTO - Data Transfer Objects). Nemaju logiku u sebi, služe isključivo za elegantno prenošenje rezultata (veličine, vremena, stringova) iz jedne klase u glavnu klasu za ispis.
