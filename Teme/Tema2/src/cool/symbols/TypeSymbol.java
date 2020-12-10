package cool.symbols;

import cool.scopes.Scope;

import java.util.HashMap;

public class TypeSymbol extends Symbol implements Scope {
	public static final TypeSymbol OBJECT = new TypeSymbol("Object", null);
	public static final TypeSymbol INT = new TypeSymbol("Int", "Object");
	public static final TypeSymbol BOOL = new TypeSymbol("Bool", "Object");
	public static final TypeSymbol STRING = new TypeSymbol("String", "Object");
	public static final TypeSymbol IO = new TypeSymbol("IO", "Object");

	private final HashMap<String, IdSymbol> attributes;
	private final HashMap<String, MethodSymbol> methods;
	private TypeSymbol parent;
	private final String parentName;

	public TypeSymbol(String name, String parentName) {
		super(name);

		this.parentName = parentName;

		attributes = new HashMap<>();
		methods = new HashMap<>();
	}

	public boolean inherits(TypeSymbol type) {
		if (this == type) {
			return true;
		}

		if (parent != null) {
			return parent.inherits(type);
		}

		return false;
	}

	public void setParent(TypeSymbol parent) {
		this.parent = parent;
	}

	public String getParentName() {
		return parentName;
	}

	@Override
	public boolean add(Symbol sym) {
		if (!(sym instanceof IdSymbol)) {
			return false;
		}

		String symbolName = sym.getName();
		if (attributes.get(symbolName) != null) {
			return false;
		}

		attributes.put(symbolName, (IdSymbol)sym);

		return true;
	}

	@Override
	public Symbol lookup(String str) {
		IdSymbol symbol = attributes.get(str);
		if (symbol != null) {
			return symbol;
		}

		if (parent != null) {
			return parent.lookup(str);
		}

		return null;
	}

	public boolean addMethod(MethodSymbol symbol) {
		String symbolName = symbol.getName();
		if (methods.get(symbolName) != null) {
			return false;
		}

		methods.put(symbolName, symbol);

		return true;
	}

	public MethodSymbol lookupMethod(String name) {
		MethodSymbol symbol = methods.get(name);
		if (symbol != null) {
			return symbol;
		}

		if (parent != null) {
			return parent.lookupMethod(name);
		}

		return null;
	}

	@Override
	public Scope getParent() {
		return parent;
	}
}
