parser grammar CPLangParser;

options {
    tokenVocab = CPLangLexer;
}

expr: IF expr THEN expr ELSE expr FI
    | INT
    | ID;
