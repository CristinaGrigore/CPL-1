import java.util.*;

// O functie este atât simbol, cât și domeniu de vizibilitate pentru parametrii
// săi formali.

// TODO 1: Implementați clasa FunctionSymbol, suprascriind metodele din interfață
// și adăugându-i un nume.
public class FunctionSymbol extends IdSymbol implements Scope {
    protected Scope parent;
    protected HashMap<String, Symbol> symbols;

    public FunctionSymbol(String name, Scope parent) {
        super(name);

        this.parent = parent;
        symbols = new HashMap<>();
    }

    @Override
    public boolean add(Symbol s) {
        if (symbols.containsKey(s.getName())) {
            return false;
        }

        symbols.put(s.getName(), s);
        return true;
    }

    @Override
    public Symbol lookup(String name) {
        Symbol s = symbols.get(name);

        if (s != null) {
            return  s;
        }

        if (parent != null) {
            return parent.lookup(name);
        }

        return null;
    }

    @Override
    public Scope getParent() {
        return parent;
    }
}