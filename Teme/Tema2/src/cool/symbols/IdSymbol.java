package cool.symbols;

public class IdSymbol extends Symbol {
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
