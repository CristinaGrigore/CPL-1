lexer grammar CPLangLexer;

IF : 'if';
THEN : 'then';
ELSE : 'else';
FI : 'fi';
fragment DIGIT : [0-9];
INT : DIGIT+;

fragment LETTER : [a-zA-Z];
TYPE : [A-Z] (LETTER | DIGIT | '_')*;
ID : (LETTER | '_')(LETTER | DIGIT | '_')*;

fragment DIGITS : DIGIT+;
fragment FRAC : ('.' DIGITS?)?;
fragment EXP : ('e' ('+' | '-')? DIGITS)?;

REAL : DIGITS FRAC EXP;

LBRACE : '{';
RBRACE : '}';

PLUS : '+';

SEMI : ';';

// *? - nongreedy
STRING : '"' ('\\"' | .)*? '"' { System.out.println("in string"); };
//STRING : '"' ('\\"' | ~'"')* '"' { System.out.println("in string"); };

BLOCK_COMMENT : '/*' (BLOCK_COMMENT | .)*? '*/';

WS : [\n\r\t ]+ -> skip;

ERROR : . { System.out.println("Error"); };