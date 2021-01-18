package cool.symbols;

import cool.scopes.Scope;

import java.util.HashMap;

public class LetSymbol implements Scope {
	private final HashMap<String, IdSymbol> vars;
	private final Scope parent;

	public LetSymbol(Scope parent) {
		this.parent = parent;
		vars = new HashMap<>();
	}

	@Override
	public boolean add(Symbol sym) {
		vars.put(sym.getName(), (IdSymbol)sym);
		return true;
	}

	@Override
	public Symbol lookup(String str) {
		Symbol symb = vars.get(str);
		if (symb == null) {
			return getParent().lookup(str);
		}

		return symb;
	}

	@Override
	public Scope getParent() {
		return parent;
	}
}
