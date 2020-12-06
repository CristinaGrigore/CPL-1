parser grammar CoolParser;

options {
	tokenVocab = CoolLexer;
}

@header{
	package cool.parser;
}

program: (classes+=classDef)* EOF;

formal: ID COLON TYPE;

varDef: ID COLON TYPE (ASSIGN value=expr)?;

classContent
	: ID COLON TYPE (ASSIGN value=expr)? SEMI																	# memberDef
	| ID LPAREN (params+=formal (COMMA params+=formal)*)? RPAREN COLON TYPE LBRACE (body+=expr)* RBRACE SEMI	# methodDef
	;

classDef: CLASS name=TYPE (INHERITS baseName=TYPE)? LBRACE (content+=classContent)* RBRACE SEMI;

caseBranch: ID COLON TYPE RARROW body=expr SEMI;

expr
	: ID LPAREN (params+=expr (COMMA params+=expr)*)? RPAREN							# implicitDispatch
    | caller=expr (AT TYPE)? DOT ID LPAREN (params+=expr (COMMA params+=expr)*)? RPAREN	# explicitDispatch
	| IF cond=expr THEN thenBranch=expr ELSE elseBranch=expr FI     					# if
	| WHILE cond=expr LOOP body=expr POOL												# while
	| LET members+=varDef (COMMA members+=varDef)* IN body=expr							# let
	| CASE var=expr OF (branches+=caseBranch)+ ESAC										# case
	| ISVOID op=expr																	# isvoid
	| NEW TYPE																			# new
	| LPAREN op=expr RPAREN																# paren
	| LBRACE (body+=expr SEMI)+ RBRACE													# block
	| NEG expr																			# neg
	| ID                                                            					# id
	| INT                                                           					# int
	| BOOL																				# bool
	| STRING																			# string
	| leftOp=expr MULT rightOp=expr														# mult
	| leftOp=expr DIV rightOp=expr														# div
	| leftOp=expr PLUS rightOp=expr														# plus
	| leftOp=expr MINUS rightOp=expr													# minus
	| leftOp=expr op=(EQUAL | LT | LE) rightOp=expr										# relOp
	| ID ASSIGN expr																	# assign
	| NOT expr																			# not
	;
