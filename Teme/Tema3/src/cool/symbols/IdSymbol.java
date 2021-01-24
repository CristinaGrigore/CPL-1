package cool.symbols;

public class IdSymbol extends Symbol {
	private TypeSymbol type;
	private int offset;
	private int defType;

	private final int ATTRIBUTE = 0;
	private final int FORMAL = 0;
	private final int LET = 0;

	public IdSymbol(String name) {
		super(name);
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

	public void makeFormal() {
		this.defType = FORMAL;
	}

	public void makeLet() {
		this.defType = LET;
	}

	public boolean isFormal() {
		return defType == FORMAL;
	}

	public boolean isAttribute() {
		return defType == ATTRIBUTE;
	}

	public boolean isLet() {
		return defType == LET;
	}

	public TypeSymbol getType() {
		return type;
	}

	public void setType(TypeSymbol type) {
		this.type = type;
	}
}
