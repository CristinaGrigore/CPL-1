package cool.scopes;

import java.io.File;

import cool.symbols.TypeSymbol;
import org.antlr.v4.runtime.*;

import cool.compiler.Compiler;
import cool.parser.CoolParser;

public class SymbolTable {
    public static GlobalScope globals;
    
    private static boolean semanticErrors;
    
    public static void defineBasicClasses() {
        globals = new GlobalScope();
        semanticErrors = false;
        
        // TODO Populate global scope.
        globals.add(TypeSymbol.OBJECT);
        globals.add(TypeSymbol.INT);
        globals.add(TypeSymbol.BOOL);
        globals.add(TypeSymbol.STRING);
        globals.add(TypeSymbol.IO);

        TypeSymbol.INT.setParent(TypeSymbol.OBJECT);
        TypeSymbol.STRING.setParent(TypeSymbol.OBJECT);
        TypeSymbol.BOOL.setParent(TypeSymbol.OBJECT);
        TypeSymbol.IO.setParent(TypeSymbol.OBJECT);

        // TODO: pune metodele fiecarei clase
    }
    
    /**
     * Displays a semantic error message.
     * 
     * @param ctx Used to determine the enclosing class context of this error,
     *            which knows the file name in which the class was defined.
     * @param info Used for line and column information.
     * @param str The error message.
     */
    public static void error(ParserRuleContext ctx, Token info, String str) {
        while (! (ctx.getParent() instanceof CoolParser.ProgramContext))
            ctx = ctx.getParent();
        
        String message = "\"" + new File(Compiler.fileNames.get(ctx)).getName()
                + "\", line " + info.getLine()
                + ":" + (info.getCharPositionInLine() + 1)
                + ", Semantic error: " + str;
        
        System.err.println(message);
        
        semanticErrors = true;
    }
    
    public static void error(String str) {
        String message = "Semantic error: " + str;
        
        System.err.println(message);
        
        semanticErrors = true;
    }
    
    public static boolean hasSemanticErrors() {
        return semanticErrors;
    }
}
