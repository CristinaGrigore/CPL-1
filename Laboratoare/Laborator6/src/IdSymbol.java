public class IdSymbol extends Symbol {
    // Fiecare identificator posedă un tip.
    protected TypeSymbol type;
    
    public IdSymbol(String name) {
        super(name);
    }
    
    public void setType(TypeSymbol type) {
        this.type = type;
    }
    
    public TypeSymbol getType() {
        return type;
    }
}