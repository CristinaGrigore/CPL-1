package cool.scopes;

import cool.symbols.Symbol;

import java.util.*;

public class GlobalScope implements Scope {
    
    private final HashMap<String, Symbol> symbols = new HashMap<>();

    @Override
    public boolean add(Symbol sym) {
        if (symbols.containsKey(sym.getName())) {
            return false;
        }
        
        symbols.put(sym.getName(), sym);
        
        return true;
    }

    @Override
    public Symbol lookup(String name) {
        return symbols.get(name);
    }

    @Override
    public Scope getParent() {
        return null;
    }
    
    @Override
    public String toString() {
        return symbols.values().toString();
    }

}
