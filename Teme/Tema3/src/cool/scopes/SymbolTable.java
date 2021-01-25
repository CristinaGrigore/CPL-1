package cool.scopes;

import java.io.File;

import cool.symbols.IdSymbol;
import cool.symbols.MethodSymbol;
import cool.symbols.TypeSymbol;
import org.antlr.v4.runtime.*;

import cool.compiler.Compiler;
import cool.parser.CoolParser;

public class SymbolTable {
    public static GlobalScope globals;
    
    private static boolean semanticErrors;

    private static void setObjectMethods() {
        var abortMethod = new MethodSymbol("abort");
        abortMethod.setParent(TypeSymbol.OBJECT);
        abortMethod.setReturnType(TypeSymbol.OBJECT);
        abortMethod.setOffset(0);

        var typeNameMethod = new MethodSymbol("type_name");
        typeNameMethod.setParent(TypeSymbol.OBJECT);
        typeNameMethod.setReturnType(TypeSymbol.STRING);
        typeNameMethod.setOffset(4);

        var copyMethod = new MethodSymbol("copy");
        copyMethod.setParent(TypeSymbol.OBJECT);
        copyMethod.setReturnType(TypeSymbol.SELF_TYPE);
        copyMethod.setOffset(8);

        TypeSymbol.OBJECT.addMethod(abortMethod);
        TypeSymbol.OBJECT.addMethod(typeNameMethod);
        TypeSymbol.OBJECT.addMethod(copyMethod);
    }

    private static void setIOMethods() {
        var outStringMethod = new MethodSymbol("out_string");
        outStringMethod.setParent(TypeSymbol.IO);
        outStringMethod.setReturnType(TypeSymbol.SELF_TYPE);
        outStringMethod.setOffset(12);
        var outStringParam = new IdSymbol("x");
        outStringParam.setType(TypeSymbol.STRING);
        outStringMethod.add(outStringParam);

        var outIntMethod = new MethodSymbol("out_int");
        outIntMethod.setParent(TypeSymbol.IO);
        outIntMethod.setReturnType(TypeSymbol.SELF_TYPE);
        outIntMethod.setOffset(16);
        var outIntParam = new IdSymbol("x");
        outIntParam.setType(TypeSymbol.INT);
        outIntMethod.add(outIntParam);

        var inStringMethod = new MethodSymbol("in_string");
        inStringMethod.setParent(TypeSymbol.IO);
        inStringMethod.setReturnType(TypeSymbol.STRING);
        inStringMethod.setOffset(20);

        var inIntMethod = new MethodSymbol("in_int");
        inIntMethod.setParent(TypeSymbol.IO);
        inIntMethod.setReturnType(TypeSymbol.INT);
        inIntMethod.setOffset(24);

        TypeSymbol.IO.addMethod(outStringMethod);
        TypeSymbol.IO.addMethod(outIntMethod);
        TypeSymbol.IO.addMethod(inStringMethod);
        TypeSymbol.IO.addMethod(inIntMethod);
    }

    private static void setStringMethods() {
        var concatMethod = new MethodSymbol("concat");
        concatMethod.setParent(TypeSymbol.STRING);
        concatMethod.setReturnType(TypeSymbol.STRING);
        concatMethod.setOffset(12);
        var concatParam = new IdSymbol("x");
        concatParam.setType(TypeSymbol.STRING);
        concatMethod.add(concatParam);

        var lengthMethod = new MethodSymbol("length");
        lengthMethod.setParent(TypeSymbol.STRING);
        lengthMethod.setReturnType(TypeSymbol.INT);
        lengthMethod.setOffset(16);

        var substrMethod = new MethodSymbol("substr");
        substrMethod.setParent(TypeSymbol.STRING);
        substrMethod.setReturnType(TypeSymbol.STRING);
        substrMethod.setOffset(20);
        var indexParam = new IdSymbol("i");
        indexParam.setType(TypeSymbol.INT);
        substrMethod.add(indexParam);
        var lenParam = new IdSymbol("l");
        lenParam.setType(TypeSymbol.INT);
        substrMethod.add(lenParam);

        TypeSymbol.STRING.addMethod(concatMethod);
        TypeSymbol.STRING.addMethod(lengthMethod);
        TypeSymbol.STRING.addMethod(substrMethod);
    }

    public static void defineBasicClasses() {
        globals = new GlobalScope();
        semanticErrors = false;
        
        globals.add(TypeSymbol.OBJECT);
        globals.add(TypeSymbol.INT);
        globals.add(TypeSymbol.BOOL);
        globals.add(TypeSymbol.STRING);
        globals.add(TypeSymbol.IO);
        globals.add(TypeSymbol.SELF_TYPE);

        TypeSymbol.OBJECT.abortChildren();
        TypeSymbol.tagCounter = 0;

        TypeSymbol.INT.setParent(TypeSymbol.OBJECT);
        TypeSymbol.STRING.setParent(TypeSymbol.OBJECT);
        TypeSymbol.BOOL.setParent(TypeSymbol.OBJECT);
        TypeSymbol.IO.setParent(TypeSymbol.OBJECT);
        TypeSymbol.SELF_TYPE.setParent(TypeSymbol.OBJECT);

        TypeSymbol.INT.add(new IdSymbol("value"));
        TypeSymbol.BOOL.add(new IdSymbol("value"));

        setObjectMethods();
        setIOMethods();
        setStringMethods();
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
