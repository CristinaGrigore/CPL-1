public class IdSymbol extends Symbol {
    // Fiecare identificator posedă un tip.
    protected TypeSymbol type;
    private boolean isFormal = false;
    private int offset;
    
    /*
     * TODO 5: definiți un parametru care să indice dacă:
     * 	simbolul e global
     * 			sau
     *  simbolul e al unui formal, și offset-ul față de $fp
     */

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isFormal() {
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