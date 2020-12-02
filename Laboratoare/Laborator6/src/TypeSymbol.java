
public class TypeSymbol extends Symbol {
    public TypeSymbol(String name) {
        super(name);
    }
    
    // Symboluri aferente tipurilor, definite global
    public static final TypeSymbol INT   = new TypeSymbol("Int");
    public static final TypeSymbol FLOAT = new TypeSymbol("Float");
    public static final TypeSymbol BOOL  = new TypeSymbol("Bool");
}
