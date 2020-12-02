import java.util.*;

// O functie este atât simbol, cât și domeniu de vizibilitate pentru parametrii
// săi formali.

// TODO 1: Implementați clasa FunctionSymbol, suprascriind metodele din interfață
// și adăugându-i un nume.
public class FunctionSymbol extends IdSymbol implements Scope {
 
    // LinkedHashMap reține ordinea adăugării.
    protected Map<String, Symbol> symbols = new LinkedHashMap<>();
    
    protected Scope parent;
    
    public FunctionSymbol(Scope parent, String name) {
        super(name);
        this.parent = parent;
    }

    @Override
    public boolean add(Symbol sym) {
        // Ne asigurăm că simbolul nu există deja în domeniul de vizibilitate
        // curent.
        if (symbols.containsKey(sym.getName()))
            return false;
        
        symbols.put(sym.getName(), sym);
        
        return true;
    }

    @Override
    public Symbol lookup(String s) {
        var sym = symbols.get(s);
        
        if (sym != null)
            return sym;
        
        // Dacă nu găsim simbolul în domeniul de vizibilitate curent, îl căutăm
        // în domeniul de deasupra.
        if (parent != null)
            return parent.lookup(s);
        
        return null;
    }

    @Override
    public Scope getParent() {
        return parent;
    }
    
    public Map<String, Symbol> getFormals() {
        return symbols;
    }
}