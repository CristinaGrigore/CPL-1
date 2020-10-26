lexer grammar CPLangLexer;

/**
 * Reguli de funcționare:
 * 
 * * Se ia în considerare cel mai lung lexem recunoscut, indiferent de ordinea
 *   regulilor din specificație (maximal munch).
 * 
 * * Dacă există mai multe cele mai lungi lexeme, se ia în considerare prima
 *   regulă din specificație.
 */
fragment DIGIT : [0-9];
fragment LOWERCASE: [a-z];
fragment UPPERCASE: [A-Z];

DELIM: ';';
COMMA: ',';

LINE_COMMENT: '//' .*? '\n' -> skip;
BLOCK_COMMENT: '/*' (BLOCK_COMMENT | .)*? ('*/' | EOF { System.err.println("ERROR: Unclosed block comment"); }) -> skip;
INCORRECT_COMMENT: '*/' { System.err.println("ERROR: Unopened block comment"); } -> skip;

/**
 * Cuvânt cheie.
 */
IF : 'if';
THEN: 'then';
ELSE: 'else';
FI: 'fi';
TRUE: 'true';
FALSE: 'false';

/**
 * Brackets.
 */
OPEN_ROUND_BRACK: '(';
CLOSED_ROUND_BRACK: ')';
OPEN_SQUARE_BRACK: '[';
CLOSED_SQUARE_BRACK: ']';
OPEN_CURLY_BRACK: '{';
CLOSED_CURLY_BRACK: '}';

/**
 * Operatori relationali.
 */
EQ: '==';
LT: '<';
LE: '<=';

/**
 * Operatori aritmetici.
 */
PLUS: '+';
MINUS: '-';
MUL: '*';
DIV: '/';
ATTRIB: '=';

/**
 * Număr întreg.
 * 
 * fragment spune că acea categorie este utilizată doar în interiorul
 * analizorului lexical, nefiind trimisă mai departe analizorului sintactic.
 */
INT: DIGIT+;

fragment NAME: (LOWERCASE | UPPERCASE | '_' | DIGIT)*;

/**
 * Tip de date.
 */
TYPE: UPPERCASE NAME;

/**
 * Identificator.
 */
ID: LOWERCASE NAME;

/**
 * Număr real.
 */
fragment DIGITS: DIGIT+;
fragment FRACTION: '.' DIGITS?;
fragment EXPONENT: ('e' ('+' | '-')? DIGITS)?;
FLOAT: (DIGITS FRACTION | DIGIT* '.' DIGITS | DIGITS) EXPONENT;

/**
 * Șir de caractere.
 * 
 * Poate conține caracterul '"', doar precedat de backslash.
 * . reprezintă orice caracter în afară de EOF.
 * *? este operatorul non-greedy, care încarcă să consume caractere cât timp
 * nu a fost întâlnit caracterul ulterior, '"'.
 * 
 * Acoladele de la final pot conține secvențe arbitrare de cod Java,
 * care vor fi executate la întâlnirea acestui token.
 */
STRING: '"' ('\\"' | .)*? '"'
    { System.out.println("there are no strings in CPLang, but shhh.."); };

/**
 * Spații albe.
 * 
 * skip spune că nu este creat niciun token pentru lexemul depistat.
 */
WS: [ \n\r\t]+ -> skip;

/**
 * Modalitate alternativă de recunoaștere a șirurilor de caractere, folosind
 * moduri lexicale.
 * 
 * Un mod lexical, precum cel implicit (DEFAULT_MODE) sau IN_STR, de mai jos,
 * reprezintă stări ale analizorului. Când analizorul se află într-un anumit
 * mod, numai regulile din acel mod se pot activa.
 * 
 * Folosim pushMode și popMode pentru intra și ieși din modurile lexicale,
 * în regim de stivă.
 * 
 * more spune că deocamdată nu este construit un token, dar lexemul identificat
 * va face parte, cumulativ, din lexemul recunoscut de următoarea regulă.
 * 
 * De-abia la recunoașterea caracterului '"' de sfârșit de șir de către regula
 * STR, se va construi un token cu categoria STR și întregul conținut al șirului
 * drept lexem.
 */

STR_OPEN: '"' -> pushMode(IN_STR), more;

mode IN_STR;

STR: '"' -> popMode;
CHAR: ('\\"' | ~'"') -> more;  // ~ = complement
