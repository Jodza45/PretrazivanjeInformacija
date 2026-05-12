# Izvestaj za laboratorijsku vezbu 1

Student: Jovan Milosevic  
Broj indeksa: 19241  
Naziv projekta: `19241_Jovan_Milosevic_lab1`

## Kolekcija dokumenata

Koriscena su 4 tekstualna fajla iz Project Gutenberg kolekcije:

| Fajl | Velicina |
| --- | ---: |
| `alice_in_wonderland.txt` | 174314 B |
| `frankenstein.txt` | 448888 B |
| `jekyll_and_hyde.txt` | 163489 B |
| `the_time_machine.txt` | 204378 B |

Svaki originalni fajl je podeljen na 100 delova, pa nova kolekcija ima 400 fajlova.

## Polja u indeksu

| Polje | Lucene tip | Namena |
| --- | --- | --- |
| `contents` | `TextField` | Sadrzaj tekstualnog fajla, koristi se za tekstualnu pretragu. |
| `filename` | `StringField` | Naziv fajla, cuva se u indeksu. |
| `fullpath` | `StringField` | Kompletna putanja do fajla, cuva se u indeksu. |
| `sizeBytes` | `LongPoint` | Velicina fajla u bajtovima, koristi se za `PointRangeQuery`. |
| `sizeBytesStored` | `StoredField` | Sacuvana velicina fajla za prikaz rezultata. |

## Rezultati indeksiranja

Rezultat poslednjeg pokretanja:

| Indeks | Broj dokumenata | Vreme kreiranja | Velicina indeksa |
| --- | ---: | ---: | ---: |
| Originalna kolekcija | 4 | 525 ms | 410098 B |
| Podeljena kolekcija | 400 | 311 ms | 481983 B |

## Boolean upit

Tekstualni oblik upita:

```text
((life AND time) OR man) AND NOT queen
```

Upit koristi sve tri trazene logicke operacije:

- `AND`: dokument mora da sadrzi termine `life` i `time` u jednom delu upita.
- `OR`: dokument moze da zadovolji podupit `(life AND time)` ili termin `man`.
- `NOT`: dokument ne sme da sadrzi termin `queen`.

Rezultati:

| Indeks | Nacin kreiranja upita | Broj pogodaka |
| --- | --- | ---: |
| Originalna kolekcija | Direktno preko objektnog modela | 2 |
| Podeljena kolekcija | Direktno preko objektnog modela | 175 |
| Originalna kolekcija | Parsiranjem tekstualnog upita | 2 |
| Podeljena kolekcija | Parsiranjem tekstualnog upita | 175 |

## PointRangeQuery

Poslednja cifra broja indeksa `19241` je `1`, pa je prema zadatku izabran `PointRangeQuery`.

Tekstualni oblik upita:

```text
sizeBytes:[100 TO 500000]
```

Direktni objektni model pravi se metodom:

```java
LongPoint.newRangeQuery("sizeBytes", 100L, 500000L)
```

Rezultati:

| Indeks | Nacin kreiranja upita | Broj pogodaka |
| --- | --- | ---: |
| Originalna kolekcija | Direktno preko objektnog modela | 4 |
| Podeljena kolekcija | Direktno preko objektnog modela | 400 |
| Originalna kolekcija | Parsiranjem tekstualnog oblika | 4 |
| Podeljena kolekcija | Parsiranjem tekstualnog oblika | 400 |
