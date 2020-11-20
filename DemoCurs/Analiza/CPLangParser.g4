parser grammar CPLangParser;

options {
    tokenVocab = CPLangLexer;
}

def : type=TYPE name=ID;

// Cu # se specifica numele fiecarei productii
// => in CPLangParserVisitor apare cate o metoda pt fiecare productie `visitNumeProd`
// => in CPLangParserListener apar 2 metode pt fiecare productie: `enterNumeProd` si `exitNumeProd`
expr: IF cond=expr THEN thenBranch=expr ELSE elseBranch=expr FI     # if
    | INT                                                           # int
    | ID                                                            # id
    | LBRACE ((def | expr) SEMI)+ RBRACE							# block
    | left=expr op=PLUS right=expr									# plus
    ;
