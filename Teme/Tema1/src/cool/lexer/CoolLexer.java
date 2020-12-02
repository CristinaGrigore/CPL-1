// Generated from /home/teo/Poli/An_4/CPL-Tema1/Teme/Tema1/src/cool/lexer/CoolLexer.g4 by ANTLR 4.8

	package cool.lexer;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CoolLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ERROR=1, IF=2, THEN=3, ELSE=4, FI=5, WHILE=6, LOOP=7, POOL=8, LET=9, IN=10, 
		NEW=11, CLASS=12, INHERITS=13, CASE=14, OF=15, RARROW=16, ESAC=17, ISVOID=18, 
		NOT=19, AT=20, BOOL=21, TYPE=22, ID=23, INT=24, STRING=25, DOT=26, SEMI=27, 
		COLON=28, COMMA=29, ASSIGN=30, LPAREN=31, RPAREN=32, LBRACE=33, RBRACE=34, 
		PLUS=35, MINUS=36, MULT=37, DIV=38, EQUAL=39, LT=40, LE=41, NEG=42, LINE_COMMENT=43, 
		BLOCK_COMMENT=44, INCORRECT_COMMENT=45, WS=46, INVALID=47;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"IF", "THEN", "ELSE", "FI", "WHILE", "LOOP", "POOL", "LET", "IN", "NEW", 
			"CLASS", "INHERITS", "CASE", "OF", "RARROW", "ESAC", "ISVOID", "NOT", 
			"AT", "BOOL", "LOWERCASE", "UPPERCASE", "NAME", "TYPE", "ID", "DIGIT", 
			"INT", "STRING", "DOT", "SEMI", "COLON", "COMMA", "ASSIGN", "LPAREN", 
			"RPAREN", "LBRACE", "RBRACE", "PLUS", "MINUS", "MULT", "DIV", "EQUAL", 
			"LT", "LE", "NEG", "NEW_LINE", "LINE_COMMENT", "BLOCK_COMMENT", "INCORRECT_COMMENT", 
			"WS", "INVALID"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'if'", "'then'", "'else'", "'fi'", "'while'", "'loop'", 
			"'pool'", "'let'", "'in'", "'new'", "'class'", "'inherits'", "'case'", 
			"'of'", "'=>'", "'esac'", "'isvoid'", "'not'", "'@'", null, null, null, 
			null, null, "'.'", "';'", "':'", "','", "'<-'", "'('", "')'", "'{'", 
			"'}'", "'+'", "'-'", "'*'", "'/'", "'='", "'<'", "'<='", "'~'", null, 
			null, "'*)'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ERROR", "IF", "THEN", "ELSE", "FI", "WHILE", "LOOP", "POOL", "LET", 
			"IN", "NEW", "CLASS", "INHERITS", "CASE", "OF", "RARROW", "ESAC", "ISVOID", 
			"NOT", "AT", "BOOL", "TYPE", "ID", "INT", "STRING", "DOT", "SEMI", "COLON", 
			"COMMA", "ASSIGN", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "PLUS", "MINUS", 
			"MULT", "DIV", "EQUAL", "LT", "LE", "NEG", "LINE_COMMENT", "BLOCK_COMMENT", 
			"INCORRECT_COMMENT", "WS", "INVALID"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


		private static final int MAX_STR_LEN = 1024;

		private void raiseError(String msg) {
			setText(msg);
			setType(ERROR);
		}


	public CoolLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CoolLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 27:
			STRING_action((RuleContext)_localctx, actionIndex);
			break;
		case 47:
			BLOCK_COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		case 48:
			INCORRECT_COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		case 50:
			INVALID_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void STRING_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:

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
				
			break;
		case 1:
			 raiseError("EOF in string constant"); 
			break;
		case 2:
			 raiseError("Unterminated string constant"); 
			break;
		}
	}
	private void BLOCK_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 3:
			 skip(); 
			break;
		case 4:
			 raiseError("EOF in comment"); 
			break;
		}
	}
	private void INCORRECT_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 5:
			 raiseError("Unmatched *)"); 
			break;
		}
	}
	private void INVALID_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 6:
			 raiseError("Invalid character: " + getText()); 
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\61\u0152\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3"+
		"\t\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3"+
		"\17\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3"+
		"\22\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3"+
		"\25\3\25\3\25\5\25\u00ca\n\25\3\26\3\26\3\27\3\27\3\30\3\30\3\30\3\30"+
		"\7\30\u00d4\n\30\f\30\16\30\u00d7\13\30\3\31\3\31\3\31\3\32\3\32\3\32"+
		"\3\33\3\33\3\34\6\34\u00e2\n\34\r\34\16\34\u00e3\3\35\3\35\3\35\3\35\3"+
		"\35\3\35\7\35\u00ec\n\35\f\35\16\35\u00ef\13\35\3\35\3\35\3\35\3\35\3"+
		"\35\3\35\3\35\5\35\u00f8\n\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\""+
		"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-"+
		"\3-\3-\3.\3.\3/\5/\u011f\n/\3/\3/\3\60\3\60\3\60\3\60\7\60\u0127\n\60"+
		"\f\60\16\60\u012a\13\60\3\60\3\60\5\60\u012e\n\60\3\60\3\60\3\61\3\61"+
		"\3\61\3\61\3\61\7\61\u0137\n\61\f\61\16\61\u013a\13\61\3\61\3\61\3\61"+
		"\3\61\3\61\3\61\5\61\u0142\n\61\3\62\3\62\3\62\3\62\3\62\3\63\6\63\u014a"+
		"\n\63\r\63\16\63\u014b\3\63\3\63\3\64\3\64\3\64\5\u00ed\u0128\u0138\2"+
		"\65\3\4\5\5\7\6\t\7\13\b\r\t\17\n\21\13\23\f\25\r\27\16\31\17\33\20\35"+
		"\21\37\22!\23#\24%\25\'\26)\27+\2-\2/\2\61\30\63\31\65\2\67\329\33;\34"+
		"=\35?\36A\37C E!G\"I#K$M%O&Q\'S(U)W*Y+[,]\2_-a.c/e\60g\61\3\2\6\3\2c|"+
		"\3\2C\\\3\2\62;\5\2\13\f\16\17\"\"\2\u015e\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2"+
		"\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2"+
		"\2)\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2"+
		"\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2"+
		"I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3"+
		"\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2"+
		"\2\2e\3\2\2\2\2g\3\2\2\2\3i\3\2\2\2\5l\3\2\2\2\7q\3\2\2\2\tv\3\2\2\2\13"+
		"y\3\2\2\2\r\177\3\2\2\2\17\u0084\3\2\2\2\21\u0089\3\2\2\2\23\u008d\3\2"+
		"\2\2\25\u0090\3\2\2\2\27\u0094\3\2\2\2\31\u009a\3\2\2\2\33\u00a3\3\2\2"+
		"\2\35\u00a8\3\2\2\2\37\u00ab\3\2\2\2!\u00ae\3\2\2\2#\u00b3\3\2\2\2%\u00ba"+
		"\3\2\2\2\'\u00be\3\2\2\2)\u00c9\3\2\2\2+\u00cb\3\2\2\2-\u00cd\3\2\2\2"+
		"/\u00d5\3\2\2\2\61\u00d8\3\2\2\2\63\u00db\3\2\2\2\65\u00de\3\2\2\2\67"+
		"\u00e1\3\2\2\29\u00e5\3\2\2\2;\u00f9\3\2\2\2=\u00fb\3\2\2\2?\u00fd\3\2"+
		"\2\2A\u00ff\3\2\2\2C\u0101\3\2\2\2E\u0104\3\2\2\2G\u0106\3\2\2\2I\u0108"+
		"\3\2\2\2K\u010a\3\2\2\2M\u010c\3\2\2\2O\u010e\3\2\2\2Q\u0110\3\2\2\2S"+
		"\u0112\3\2\2\2U\u0114\3\2\2\2W\u0116\3\2\2\2Y\u0118\3\2\2\2[\u011b\3\2"+
		"\2\2]\u011e\3\2\2\2_\u0122\3\2\2\2a\u0131\3\2\2\2c\u0143\3\2\2\2e\u0149"+
		"\3\2\2\2g\u014f\3\2\2\2ij\7k\2\2jk\7h\2\2k\4\3\2\2\2lm\7v\2\2mn\7j\2\2"+
		"no\7g\2\2op\7p\2\2p\6\3\2\2\2qr\7g\2\2rs\7n\2\2st\7u\2\2tu\7g\2\2u\b\3"+
		"\2\2\2vw\7h\2\2wx\7k\2\2x\n\3\2\2\2yz\7y\2\2z{\7j\2\2{|\7k\2\2|}\7n\2"+
		"\2}~\7g\2\2~\f\3\2\2\2\177\u0080\7n\2\2\u0080\u0081\7q\2\2\u0081\u0082"+
		"\7q\2\2\u0082\u0083\7r\2\2\u0083\16\3\2\2\2\u0084\u0085\7r\2\2\u0085\u0086"+
		"\7q\2\2\u0086\u0087\7q\2\2\u0087\u0088\7n\2\2\u0088\20\3\2\2\2\u0089\u008a"+
		"\7n\2\2\u008a\u008b\7g\2\2\u008b\u008c\7v\2\2\u008c\22\3\2\2\2\u008d\u008e"+
		"\7k\2\2\u008e\u008f\7p\2\2\u008f\24\3\2\2\2\u0090\u0091\7p\2\2\u0091\u0092"+
		"\7g\2\2\u0092\u0093\7y\2\2\u0093\26\3\2\2\2\u0094\u0095\7e\2\2\u0095\u0096"+
		"\7n\2\2\u0096\u0097\7c\2\2\u0097\u0098\7u\2\2\u0098\u0099\7u\2\2\u0099"+
		"\30\3\2\2\2\u009a\u009b\7k\2\2\u009b\u009c\7p\2\2\u009c\u009d\7j\2\2\u009d"+
		"\u009e\7g\2\2\u009e\u009f\7t\2\2\u009f\u00a0\7k\2\2\u00a0\u00a1\7v\2\2"+
		"\u00a1\u00a2\7u\2\2\u00a2\32\3\2\2\2\u00a3\u00a4\7e\2\2\u00a4\u00a5\7"+
		"c\2\2\u00a5\u00a6\7u\2\2\u00a6\u00a7\7g\2\2\u00a7\34\3\2\2\2\u00a8\u00a9"+
		"\7q\2\2\u00a9\u00aa\7h\2\2\u00aa\36\3\2\2\2\u00ab\u00ac\7?\2\2\u00ac\u00ad"+
		"\7@\2\2\u00ad \3\2\2\2\u00ae\u00af\7g\2\2\u00af\u00b0\7u\2\2\u00b0\u00b1"+
		"\7c\2\2\u00b1\u00b2\7e\2\2\u00b2\"\3\2\2\2\u00b3\u00b4\7k\2\2\u00b4\u00b5"+
		"\7u\2\2\u00b5\u00b6\7x\2\2\u00b6\u00b7\7q\2\2\u00b7\u00b8\7k\2\2\u00b8"+
		"\u00b9\7f\2\2\u00b9$\3\2\2\2\u00ba\u00bb\7p\2\2\u00bb\u00bc\7q\2\2\u00bc"+
		"\u00bd\7v\2\2\u00bd&\3\2\2\2\u00be\u00bf\7B\2\2\u00bf(\3\2\2\2\u00c0\u00c1"+
		"\7v\2\2\u00c1\u00c2\7t\2\2\u00c2\u00c3\7w\2\2\u00c3\u00ca\7g\2\2\u00c4"+
		"\u00c5\7h\2\2\u00c5\u00c6\7c\2\2\u00c6\u00c7\7n\2\2\u00c7\u00c8\7u\2\2"+
		"\u00c8\u00ca\7g\2\2\u00c9\u00c0\3\2\2\2\u00c9\u00c4\3\2\2\2\u00ca*\3\2"+
		"\2\2\u00cb\u00cc\t\2\2\2\u00cc,\3\2\2\2\u00cd\u00ce\t\3\2\2\u00ce.\3\2"+
		"\2\2\u00cf\u00d4\5+\26\2\u00d0\u00d4\5-\27\2\u00d1\u00d4\7a\2\2\u00d2"+
		"\u00d4\5\65\33\2\u00d3\u00cf\3\2\2\2\u00d3\u00d0\3\2\2\2\u00d3\u00d1\3"+
		"\2\2\2\u00d3\u00d2\3\2\2\2\u00d4\u00d7\3\2\2\2\u00d5\u00d3\3\2\2\2\u00d5"+
		"\u00d6\3\2\2\2\u00d6\60\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d8\u00d9\5-\27"+
		"\2\u00d9\u00da\5/\30\2\u00da\62\3\2\2\2\u00db\u00dc\5+\26\2\u00dc\u00dd"+
		"\5/\30\2\u00dd\64\3\2\2\2\u00de\u00df\t\4\2\2\u00df\66\3\2\2\2\u00e0\u00e2"+
		"\5\65\33\2\u00e1\u00e0\3\2\2\2\u00e2\u00e3\3\2\2\2\u00e3\u00e1\3\2\2\2"+
		"\u00e3\u00e4\3\2\2\2\u00e48\3\2\2\2\u00e5\u00ed\7$\2\2\u00e6\u00e7\7^"+
		"\2\2\u00e7\u00ec\7$\2\2\u00e8\u00e9\7^\2\2\u00e9\u00ec\5]/\2\u00ea\u00ec"+
		"\13\2\2\2\u00eb\u00e6\3\2\2\2\u00eb\u00e8\3\2\2\2\u00eb\u00ea\3\2\2\2"+
		"\u00ec\u00ef\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ee\u00f7"+
		"\3\2\2\2\u00ef\u00ed\3\2\2\2\u00f0\u00f1\7$\2\2\u00f1\u00f8\b\35\2\2\u00f2"+
		"\u00f3\7\2\2\3\u00f3\u00f8\b\35\3\2\u00f4\u00f5\5]/\2\u00f5\u00f6\b\35"+
		"\4\2\u00f6\u00f8\3\2\2\2\u00f7\u00f0\3\2\2\2\u00f7\u00f2\3\2\2\2\u00f7"+
		"\u00f4\3\2\2\2\u00f8:\3\2\2\2\u00f9\u00fa\7\60\2\2\u00fa<\3\2\2\2\u00fb"+
		"\u00fc\7=\2\2\u00fc>\3\2\2\2\u00fd\u00fe\7<\2\2\u00fe@\3\2\2\2\u00ff\u0100"+
		"\7.\2\2\u0100B\3\2\2\2\u0101\u0102\7>\2\2\u0102\u0103\7/\2\2\u0103D\3"+
		"\2\2\2\u0104\u0105\7*\2\2\u0105F\3\2\2\2\u0106\u0107\7+\2\2\u0107H\3\2"+
		"\2\2\u0108\u0109\7}\2\2\u0109J\3\2\2\2\u010a\u010b\7\177\2\2\u010bL\3"+
		"\2\2\2\u010c\u010d\7-\2\2\u010dN\3\2\2\2\u010e\u010f\7/\2\2\u010fP\3\2"+
		"\2\2\u0110\u0111\7,\2\2\u0111R\3\2\2\2\u0112\u0113\7\61\2\2\u0113T\3\2"+
		"\2\2\u0114\u0115\7?\2\2\u0115V\3\2\2\2\u0116\u0117\7>\2\2\u0117X\3\2\2"+
		"\2\u0118\u0119\7>\2\2\u0119\u011a\7?\2\2\u011aZ\3\2\2\2\u011b\u011c\7"+
		"\u0080\2\2\u011c\\\3\2\2\2\u011d\u011f\7\17\2\2\u011e\u011d\3\2\2\2\u011e"+
		"\u011f\3\2\2\2\u011f\u0120\3\2\2\2\u0120\u0121\7\f\2\2\u0121^\3\2\2\2"+
		"\u0122\u0123\7/\2\2\u0123\u0124\7/\2\2\u0124\u0128\3\2\2\2\u0125\u0127"+
		"\13\2\2\2\u0126\u0125\3\2\2\2\u0127\u012a\3\2\2\2\u0128\u0129\3\2\2\2"+
		"\u0128\u0126\3\2\2\2\u0129\u012d\3\2\2\2\u012a\u0128\3\2\2\2\u012b\u012e"+
		"\5]/\2\u012c\u012e\7\2\2\3\u012d\u012b\3\2\2\2\u012d\u012c\3\2\2\2\u012e"+
		"\u012f\3\2\2\2\u012f\u0130\b\60\5\2\u0130`\3\2\2\2\u0131\u0132\7*\2\2"+
		"\u0132\u0133\7,\2\2\u0133\u0138\3\2\2\2\u0134\u0137\5a\61\2\u0135\u0137"+
		"\13\2\2\2\u0136\u0134\3\2\2\2\u0136\u0135\3\2\2\2\u0137\u013a\3\2\2\2"+
		"\u0138\u0139\3\2\2\2\u0138\u0136\3\2\2\2\u0139\u0141\3\2\2\2\u013a\u0138"+
		"\3\2\2\2\u013b\u013c\7,\2\2\u013c\u013d\7+\2\2\u013d\u013e\3\2\2\2\u013e"+
		"\u0142\b\61\6\2\u013f\u0140\7\2\2\3\u0140\u0142\b\61\7\2\u0141\u013b\3"+
		"\2\2\2\u0141\u013f\3\2\2\2\u0142b\3\2\2\2\u0143\u0144\7,\2\2\u0144\u0145"+
		"\7+\2\2\u0145\u0146\3\2\2\2\u0146\u0147\b\62\b\2\u0147d\3\2\2\2\u0148"+
		"\u014a\t\5\2\2\u0149\u0148\3\2\2\2\u014a\u014b\3\2\2\2\u014b\u0149\3\2"+
		"\2\2\u014b\u014c\3\2\2\2\u014c\u014d\3\2\2\2\u014d\u014e\b\63\5\2\u014e"+
		"f\3\2\2\2\u014f\u0150\13\2\2\2\u0150\u0151\b\64\t\2\u0151h\3\2\2\2\21"+
		"\2\u00c9\u00d3\u00d5\u00e3\u00eb\u00ed\u00f7\u011e\u0128\u012d\u0136\u0138"+
		"\u0141\u014b\n\3\35\2\3\35\3\3\35\4\b\2\2\3\61\5\3\61\6\3\62\7\3\64\b";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}