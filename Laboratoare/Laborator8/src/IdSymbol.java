public class IdSymbol extends Symbol {
    // Fiecare identificator posedă un tip.
    protected TypeSymbol type;
    private boolean isFormal = false;
    
    /*
     * TODO 5: definiți un parametru care să indice dacă:
     * 	simbolul e global
     * 			sau
     *  simbolul e al unui formal, și offset-ul față de $fp
     */

    public boolean isGlobal() {
        return isFormal;
    }

    public void setFormal() {
        isFormal = true;
    }

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