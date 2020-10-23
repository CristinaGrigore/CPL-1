lexer grammar CPLangLexer;

IF: 'if';
THEN: 'then';
FI: 'fi';
ELSE: 'else';

WS: [\n\r\t ]+ -> skip;

fragment DIGIT: [0-9];
INT: DIGIT+;

fragment LETTER: [a-zA-Z];
ID: (LETTER | '_')(LETTER | DIGIT | '_')*;

fragment DIGITS: DIGIT+;
fragment FRAC: ('.' DIGITS?)?;
fragment EXP: ('e' ('+' | '-')? DIGITS)?;
REAL: DIGIT+ FRAC EXP;

// . face match pe orice in afara de EOF, dar poate face match si pe "
// Cu codul Java inline se pot prelucra tokenii inainte sa-i trimitem analizei sintactice
// STRING: '"' ('\\"' | ~'"')* '"' { System.out.println("in.string"); };

// *? = * non-greedy care consuma pana intalneste caracterul din dreapta, fara sa-l consume impreuna cu cel din stanga
// Ambele reguli STRING se comporta la fel
STRING: '"' ('\\"' | .)*? '"' { System.out.println("in.string"); };

// E nevoie de *? pentru ca nu se pot nega secvente de caractere, ci doar unul singur
// Pentru comentarii imbricate se pot folosi MODURI LEXICALE: o stiva de moduri
//  un mod = colectie de reguli
//  cel implicit = default
// Se pot folosi si REGULI RECURSIVE:
BLOCK_COMMENT: '/*' (BLOCK_COMMENT | .)*? '*/';

// Pentru tokeni nedefiniti
// Functioneaza pentru ca se face match pe reguli in ordinea in care sunt definite
ERROR: . { System.out.println("Incorrect formatting"); };
