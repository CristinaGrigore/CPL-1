package cool.symbols;

public class IdSymbol extends Symbol {
	private TypeSymbol type;
	private int offset;
	private int defType;

	private final int ATTRIBUTE = 1;

	public IdSymbol(String name) {
		super(name);
		defType = 0;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void makeAttribute() {
		this.defType = ATTRIBUTE;
	}

	public boolean isAttribute() {
		return defType == ATTRIBUTE;
	}

	public TypeSymbol getType() {
		return type;
	}

	public void setType(TypeSymbol type) {
		this.type = type;
	}
}
