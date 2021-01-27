package cool.symbols;

import cool.scopes.Scope;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.*;
import java.util.stream.Collectors;

public class TypeSymbol extends Symbol implements Scope {
	public static final TypeSymbol OBJECT = new TypeSymbol("Object", null);
	public static final TypeSymbol INT = new TypeSymbol("Int", "Object");
	public static final TypeSymbol BOOL = new TypeSymbol("Bool", "Object");
	public static final TypeSymbol STRING = new TypeSymbol("String", "Object");
	public static final TypeSymbol IO = new TypeSymbol("IO", "Object");
	public static final TypeSymbol SELF_TYPE = new TypeSymbol("SELF_TYPE", "Object");

	public static int tagCounter;

	private final LinkedHashMap<String, IdSymbol> attributes;
	private final LinkedHashMap<String, MethodSymbol> methods;
	private final HashSet<TypeSymbol> children;
	private TypeSymbol parent;
	private final String parentName;
	private int tag;
	private int maxTag;

	public TypeSymbol(String name, String parentName) {
		super(name);
		this.parentName = parentName;

		attributes = new LinkedHashMap<>();
		methods = new LinkedHashMap<>();
		children = new HashSet<>();

		var self = new IdSymbol("self");
		self.setType(SELF_TYPE);
		attributes.put(self.getName(), self);
	}

	public int setTags() {
		tag = tagCounter;
		maxTag = tagCounter;

		for (var child : children) {
			if (child != SELF_TYPE) {
				++tagCounter;
				int childTag = child.setTags();
				if (maxTag < childTag) {
					maxTag = childTag;
				}
			}
		}

		return maxTag;
	}

	public void abortChildren() {
		children.clear();
	}

	public int getTag() {
		return tag;
	}

	public int getMaxTag() {
		return maxTag;
	}

	public int getParentNumMethods() {
		TypeSymbol par = parent;
		HashSet<String> parentMethods = new HashSet<>();

		while (par != null) {
			parentMethods.addAll(par.methods.values().stream().map(MethodSymbol::getName).collect(Collectors.toList()));
			par = par.parent;
		}

		return parentMethods.size();
	}

	public int getNumAttrib() {
		int totalAttrib = (attributes.size() - 1) * 4;
		if (parent != null) {
			totalAttrib += parent.getNumAttrib();
		}

		return totalAttrib;
	}

	private List<String> getDispTabMethods() {
		var className = getName();
		var currentMethods = methods.keySet();
		List<String> dispTabMethods;

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

	private String getClassAttribST() {
		if (this == TypeSymbol.INT || this == TypeSymbol.BOOL) {
			return "\t.word\t0";
		} else if (this == TypeSymbol.STRING) {
			return "\t.word\tint_const_0\n\t.asciiz\t\"\"\n\t.align\t2";
		}

		String attrib = "";
		if (parent != null) {
			attrib = parent.getClassAttribST();
		}

		var crtAttrib = attributes
				.values()
				.stream()
				.filter(attr -> !attr.getName().equals("self"))
				.map(attr -> {
					var type = attr.getType();

					if (type == TypeSymbol.STRING) {
						return "\t.word\tstr_const_0";
					} else if (type == TypeSymbol.INT) {
						return "\t.word\tint_const_0";
					} else if (type == TypeSymbol.BOOL) {
						return "\t.word\tbool_const_false";
					} else {
						return "\t.word\t0";
					}
				}).collect(Collectors.joining("\n"));

		if (!attrib.isEmpty() && !crtAttrib.isEmpty()) {
			return attrib + "\n" + crtAttrib;
		}
		return attrib + crtAttrib;
	}

	public ST getProtObj(STGroupFile templates) {
		int words = 3;
		var type = this;

		while (type != null) {
			words += type.attributes.size() - 1;
			type = type.parent;
		}

		return templates.getInstanceOf("protObj")
				.add("class", this)
				.add("tag", tag)
				.add("words", words)
				.add("attrib", getClassAttribST());
	}

	public ST getInitMethod(STGroupFile templates) {
		return templates.getInstanceOf("initMethod")
				.add("class", this)
				.add("parent", getParentName());
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
		if (this != SELF_TYPE) {
			parent.children.add(this);
		}
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
