public interface Scope {
	// ret F => exista deja simb in scope
	boolean add(Symbol s);

	// Cauta simb pe baza numelui
	Symbol lookup(String name);

	// Ret scopul parinte
	Scope getParent();
}
