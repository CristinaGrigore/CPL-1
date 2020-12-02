
public interface Scope {
    // Adaugă un simbol în domeniul de vizibilitate curent.
    public boolean add(Symbol s);
    
    // Caută un simbol în domeniul de vizibilitate curent sau în cele superioare.
    public Symbol lookup(String s);
    
    // Întoarce domeniul de vizibilitate de deasupra.
    public Scope getParent();
}
