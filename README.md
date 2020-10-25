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

### Laborator 2 - Analiza Lexicala
Se parseaza si se scriu intr-un fisier de output tokenii dintr-un fisier sursa
scris in `CPLang`.
