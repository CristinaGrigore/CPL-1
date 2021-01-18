package cool.scopes;

import cool.symbols.IdSymbol;
import cool.symbols.Symbol;

public class CaseScope implements Scope {
	private final Scope parent;
	private IdSymbol var;

	public CaseScope(Scope parent) {
		this.parent = parent;
	}

	@Override
	public boolean add(Symbol sym) {
		if (!(sym instanceof IdSymbol)) {
			return false;
		}

		var = (IdSymbol)sym;

		return true;
	}

	@Override
	public Symbol lookup(String str) {
		if (var.getName().equals(str)) {
			return var;
		}

		return parent.lookup(str);
	}

	@Override
	public Scope getParent() {
		return parent;
	}
}
