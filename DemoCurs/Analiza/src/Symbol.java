public abstract class Symbol {
	protected String name;

	public Symbol(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}

class IdSymbol extends Symbol {
	protected TypeSymbol type;

	public IdSymbol(String name) {
		super(name);
	}

	public TypeSymbol getType() {
		return type;
	}

	public void setType(TypeSymbol type) {
		this.type = type;
	}
}

class TypeSymbol extends Symbol {
	public TypeSymbol(String name) {
		super(name);
	}

	public static TypeSymbol INT = new TypeSymbol("Int");
	public static TypeSymbol BOOL = new TypeSymbol("Bool");
}
