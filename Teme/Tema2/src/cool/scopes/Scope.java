package cool.scopes;

import cool.symbols.Symbol;

public interface Scope {
    public boolean add(Symbol sym);
    
    public Symbol lookup(String str);
    
    public Scope getParent();
}
