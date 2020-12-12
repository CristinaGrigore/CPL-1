# CPL
Compilatoare - UPB 2020-2021


## Demo-uri
Demo-urile facute la curs.

### Demo COOL
Introducere in COOL:
- exemplificare `SELF_TYPE`
- implementare `List`
- blocuri si `case`-uri

### Demo Analiza Lexicala
Se implementeaza o gramatica minimalista pentru `CPLang`. Gramatica parseaza
`Int`-uri, `Float`-uri, `String`-uri, comentarii, variabile, spatii si erori.

### Demo Analiza Sintactica
Se parseaza expresii de tipul `if <expr> then <expr> else <expr>`.

### Demo Arbori Abstracti de Sintaxa
Folosindu-se interfetele `Listener` si `Visitor` generate de *ANTLR*, se
implementeaza clase pentru tipurile de noduri definite pana acum (`ID`, `IF`,
`INT`) si se genereaza un arbore abstract de sintaxa ce contine noduri de aceste
tipuri.


## Laboratoare
### Laborator 0 - Setup
Introducere in ANTLR: se creeaza o gramatica simpla care accepta
`"Hello {nume}"`.

### Laborator 1 - COOL
Se implementeaza urmatoarele exercitii in `COOL` cu scopul familiarizarii cu
sintaxa limbajului:
1. Gasirea celui de-al `n`-lea termen Fibonacci atat recursiv, cat si iterativ
2. Adaugarea metodelor `reverse()` si `append()` pentru clasa `List`, ce
implementeaza o lista inlantuita + **bonus:**
[FizzBuzz](https://gist.github.com/jaysonrowe/1592775)
3. Funcionalele `map()` si `filter()` particularizate astfel:
	- `map(+ 1, l)`
	- `filter(isEven, l)`

### Laborator 2 - Analiza lexicala
Se parseaza si se scriu intr-un fisier de output tokenii dintr-un fisier sursa
scris in `CPLang`.

### Laboratoar 3, 4 - Analiza sintactica
Se construieste *AST*-ul pentru un snippet in limbajul `CPLang` folosind
interfata (visitorii) generata de `ANTLR` si se afiseaza acest arbore folosind
un visitor implementat (aproape) de la 0.

### Laborator 5, 6 - Analiza semantica
Se atribuie cate un simbol fiecarui nod ce reprezinta o instructiune (apel de
functie) sau variabila. Aceste simboluri retin tipurile de date ale expresiilor
carora le-au fost atribuite si sunt folosite pentru a realiza tiparea acestora.
Se scriu erori la `stderr` in cazul folosirii unor simboluri ce nu sunt definite
in scope-ul curent sau a folosirii unor operatori (aritmetici sau parametri de
functie) cu tipuri incompatibile.

### Laborator 7 - Assembly
Recapitulare *Assembly*, dar de data asta in varianta *MIPS* 🤮. Niste probleme
chioare si atat. Codul (in afara de bonus) se poate rula in `spim` atfel:
```bash
spim -file <fisier.s>
```


## Teme
### Tema 0 - COOL
Operatii de adaugare, stergere, `filter`, sortare si afisare aplicate pe liste,
cu scopul intelegerii limbajului `COOL`. Un tractor, dar un tractor necesar...

### Tema 1 - Analiza Lexicala si Sintactica
Se implementeaza un parser ce foloseste patternul *Visitor*, ca in
[demo-ul de la curs](https://github.com/teodutu/CPL/tree/main/DemoCurs/Analiza).
Se afiseaza arborele de expresie care rezulta din parsarea codului din fisierul
de input.
