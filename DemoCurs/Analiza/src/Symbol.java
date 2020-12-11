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
	// Adaugam aici informatii despre stocarea variabilei:
	//  * stiva => offsetul fata de $fp
	//  * .data => offsetul fata de startul sectiunii
	//  * heap => poate faptu' ca e un ptr ca sa stim dupa sa-l dereferentiem?

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
