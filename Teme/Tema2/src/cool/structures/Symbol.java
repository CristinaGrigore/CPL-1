package cool.structures;

public class Symbol {
    protected String name;
    
    public Symbol(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
