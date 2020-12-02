lexer grammar CoolLexer;

tokens { ERROR } 

@header{
	package cool.lexer;
}

@members{
	private static final int MAX_STR_LEN = 1024;

	private void raiseError(String msg) {
		setText(msg);
		setType(ERROR);
	}
}

IF: 'if';
THEN: 'then';
ELSE: 'else';
FI: 'fi';
WHILE: 'while';
LOOP: 'loop';
POOL: 'pool';
LET: 'let';
IN: 'in';
NEW: 'new';
CLASS: 'class';
INHERITS: 'inherits';
CASE: 'case';
OF: 'of';
RARROW: '=>';
ESAC: 'esac';
ISVOID: 'isvoid';
NOT: 'not';
AT: '@';

BOOL: 'true' | 'false';

fragment LOWERCASE: [a-z];
fragment UPPERCASE: [A-Z];
fragment NAME: (LOWERCASE | UPPERCASE | '_' | DIGIT)*;

TYPE: UPPERCASE NAME;

ID: LOWERCASE NAME;

fragment DIGIT: [0-9];
INT: DIGIT+;

STRING: '"' ('\\"' | '\\' NEW_LINE | .)*? (
	'"' {
		String str = getText();
		str = str
			.substring(1, str.length() - 1)
			.replace("\\\r\n", "\r\n")
			.replace("\\\n", "\n")
			.replace("\\n", "\n")
			.replace("\\t", "\t")
			.replaceAll("\\\\(?!\\\\)", "");

		if (str.length() > MAX_STR_LEN) {
			raiseError("String constant too long");
		} else if (str.contains("\0")) {
			raiseError("String contains null character");
		} else {
			setText(str);
		}
	}
	| EOF { raiseError("EOF in string constant"); }
	| NEW_LINE { raiseError("Unterminated string constant"); }
);

DOT: '.';
SEMI: ';';
COLON: ':';
COMMA: ',';
ASSIGN: '<-';
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';
PLUS: '+';
MINUS: '-';
MULT: '*';
DIV: '/';
EQUAL: '=';
LT: '<';
LE: '<=';
NEG: '~';

fragment NEW_LINE : '\r'? '\n';

LINE_COMMENT: '--' .*? (NEW_LINE | EOF) -> skip;

BLOCK_COMMENT: '(*' (BLOCK_COMMENT | .)*? (
	'*)' { skip(); }
	| EOF { raiseError("EOF in comment"); }
);
INCORRECT_COMMENT: '*)' { raiseError("Unmatched *)"); };

WS:	[ \n\f\r\t]+ -> skip;

INVALID: . { raiseError("Invalid character: " + getText()); };
