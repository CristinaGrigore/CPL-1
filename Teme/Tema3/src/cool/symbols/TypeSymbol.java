package cool.symbols;

import cool.scopes.Scope;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class TypeSymbol extends Symbol implements Scope {
	public static final TypeSymbol OBJECT = new TypeSymbol("Object", null);
	public static final TypeSymbol INT = new TypeSymbol("Int", "Object");
	public static final TypeSymbol BOOL = new TypeSymbol("Bool", "Object");
	public static final TypeSymbol STRING = new TypeSymbol("String", "Object");
	public static final TypeSymbol IO = new TypeSymbol("IO", "Object");
	public static final TypeSymbol SELF_TYPE = new TypeSymbol("SELF_TYPE", "Object");

	// TODO: pune tag conform ierarhiei de clase
	public static int tagCounter = 0;

	private final HashMap<String, IdSymbol> attributes;
	private final HashMap<String, MethodSymbol> methods;
	private TypeSymbol parent;
	private final String parentName;
	private final int tag;
	private int numMethods;

	public TypeSymbol(String name, String parentName) {
		super(name);
		this.parentName = parentName;
		tag = tagCounter++;

		attributes = new HashMap<>();
		methods = new HashMap<>();

		var self = new IdSymbol("self");
		self.setType(SELF_TYPE);
		attributes.put(self.getName(), self);
	}

	public int getTotalNumMethods() {
		int totalMethods = numMethods;
		if (parent != null) {
			totalMethods += parent.getTotalNumMethods();
		}

		return totalMethods;
	}

	private List<String> getDispTabMethods() {
		var className = getName();
		var currentMethods = methods.keySet();
		List<String> dispTabMethods;

		// TODO: salveaza offsetul metodei in MethodSymbol
		if (parent != null) {
			var parentMethods = parent.getDispTabMethods();
			dispTabMethods = parentMethods.stream().map(method -> {
				var methodName = method.substring(method.lastIndexOf(".") + 1);

				if (currentMethods.contains(methodName)) {
					currentMethods.remove(methodName);
					return className + "." + methodName;
				}
				return method;
			}).collect(Collectors.toList());
		} else {
			dispTabMethods = new ArrayList<>();
		}

		dispTabMethods.addAll(
				currentMethods.stream().map(method -> className + "." + method).collect(Collectors.toList())
		);

		return dispTabMethods;
	}

	public ST getDispTable(STGroupFile templates) {
		var dispTable = templates.getInstanceOf("sequence");
		getDispTabMethods().forEach(method -> dispTable.add(
				"e",
				templates
						.getInstanceOf("dispTableEntry")
						.add("method", method)
		));

		return templates.getInstanceOf("dispatchTable")
				.add("class", getName())
				.add("methods", dispTable);
	}

	public ST getProtObj(STGroupFile templates) {
		int words;
		String attrib;

		if (this == TypeSymbol.STRING) {
			words = 5;
			attrib = "\t.word\tint_const_0\n\t.asciiz \"\"";
		} else if (this == TypeSymbol.BOOL || this == TypeSymbol.INT) {
			words = 4;
			attrib = "\t.word\t0";
		} else {
			words = 3 + attributes.size();
			// TODO: pune attrib cu tot cu parinti
			attrib = "";
		}

		return templates.getInstanceOf("protObj")
				.add("class", this)
				.add("tag", tag)
				.add("words", words)
				.add("attrib", attrib);
	}

	public ST getAttribInitST(STGroupFile templates) {
		return null;
	}

	public ST getInitMethod(STGroupFile templates) {
		return templates.getInstanceOf("initMethod")
				.add("class", this)
				.add("parent", getParentName())
				.add("attrib", getAttribInitST(templates));
	}

	private static boolean isEqSpecial(TypeSymbol ts) {
		return ts == INT || ts == BOOL || ts == STRING;
	}

	public static boolean notEqCompatible(TypeSymbol ts1, TypeSymbol ts2) {
		if (isEqSpecial(ts1) || isEqSpecial(ts2)) {
			return ts1 != ts2;
		}

		return false;
	}

	public static TypeSymbol getLCA(TypeSymbol ts1, TypeSymbol ts2) {
		var ancestors = new HashSet<TypeSymbol>();

		while (ts1 != null) {
			ancestors.add(ts1);
			ts1 = ts1.parent;
		}

		while (ts2 != null) {
			if (ancestors.contains(ts2)) {
				return ts2;
			}

			ts2 = ts2.parent;
		}

		return TypeSymbol.OBJECT;
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

		if (parent != null && parent.lookupMethod(symbolName) == null) {
			++numMethods;
		}

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
