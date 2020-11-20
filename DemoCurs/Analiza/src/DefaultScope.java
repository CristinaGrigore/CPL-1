import java.util.LinkedHashMap;

public class DefaultScope implements Scope {
	protected Scope parent;

	// pt ca ne da si ordinea cheilor
	protected LinkedHashMap<String, Symbol> symbols = new LinkedHashMap<>();

	public DefaultScope(Scope parent) {
		this.parent = parent;
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
