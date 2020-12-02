public class DefinitionPassVisitor implements ASTVisitor<Void> {
    Scope currentScope = null;

    @Override
    public Void visit(Program prog) {
        // Domeniul de vizibilitate global conține inițial doar numele
        // tipurilor.
        currentScope = new DefaultScope(null);
        currentScope.add(TypeSymbol.INT);
        currentScope.add(TypeSymbol.FLOAT);
        currentScope.add(TypeSymbol.BOOL);

        for (var stmt: prog.stmts)
            stmt.accept(this);

        return null;
    }

    @Override
    public Void visit(Id id) {
        // Căutăm simbolul în domeniul curent.
        var symbol = (IdSymbol)currentScope.lookup(id.getToken().getText());

        id.setScope(currentScope);

        // Semnalăm eroare dacă nu există.
        if (symbol == null) {
            ASTVisitor.error(id.getToken(),
                  id.getToken().getText() + " undefined");
            return null;
        }

        // Atașăm simbolul nodului din arbore.
        id.setSymbol(symbol);

        return null;
    }

    @Override
    public Void visit(VarDef varDef) {
        // La definirea unei variabile, creăm un nou simbol.
        // Adăugăm simbolul în domeniul de vizibilitate curent.

        var id   = varDef.id;
        var type = varDef.type;

        var symbol = new IdSymbol(id.getToken().getText());

        // Semnalăm eroare dacă există deja variabila în scope-ul curent.
        if (! currentScope.add(symbol)) {
            ASTVisitor.error(id.getToken(),
                  id.getToken().getText() + " redefined");
            return null;
        }

        // Atașăm simbolul nodului din arbore.
        id.setSymbol(symbol);

        // TODO 1: Reținem informația de tip în simbolul nou creat.
        // Căutăm tipul variabilei.
        var varSymb = currentScope.lookup(type.getToken().getText());

        // Semnalăm eroare dacă nu există.
        if (!(varSymb instanceof TypeSymbol)) {
            ASTVisitor.error(
                    id.getToken(),
                    "type " + id.getToken().getText() + " undefined");
            return null;
        }

        // Reținem informația de tip în cadrul simbolului aferent variabilei
        symbol.setType((TypeSymbol)varSymb);

        if (varDef.initValue != null)
            varDef.initValue.accept(this);

        // Tipul unei definiții ca instrucțiune în sine nu este relevant.
        return null;
    }

    @Override
    public Void visit(FuncDef funcDef) {
        // Asemeni variabilelor globale, vom defini un nou simbol
        // pentru funcții. Acest nou FunctionSymbol va avea că părinte scope-ul
        // curent currentScope și va avea numele funcției.
        //
        // Nu uitați să updatati scope-ul curent înainte să fie parcurs corpul funcției,
        // și să îl restaurati la loc după ce acesta a fost parcurs.
        var id   = funcDef.id;
        var type = funcDef.type;

        var functionSymbol = new FunctionSymbol(currentScope, id.getToken().getText());
        currentScope = functionSymbol;

        // Verificăm faptul că o funcție cu același nume nu a mai fost
        // definită până acum.
        if (! currentScope.getParent().add(functionSymbol)) {
            ASTVisitor.error(
                    id.getToken(),
                    id.getToken().getText() + " function redefined"
            );
            return null;
        }

        id.setSymbol(functionSymbol);
        id.setScope(currentScope);

        // TODO 1: Reținem informația de tip în simbolul nou creat.
        // Căutăm tipul funcției.
        var funcType = currentScope.lookup(type.getToken().getText());

        // Semnalăm eroare dacă nu există.
        if (!(funcType instanceof TypeSymbol)) {
            ASTVisitor.error(
                    type.getToken(),
                    "type " + type.getToken().getText() + " undefined"
            );
            return null;
        }

        // Reținem informația de tip în cadrul simbolului aferent funcției.
        functionSymbol.setType((TypeSymbol)funcType);

        for (var formal: funcDef.formals) {
            formal.accept(this);
        }

        funcDef.body.accept(this);

        currentScope = currentScope.getParent();
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        // La definirea unei variabile, creăm un nou simbol.
        // Adăugăm simbolul în domeniul de vizibilitate curent.
        // Atașăm simbolul nodului din arbore si setăm scope-ul
        // pe variabila de tip Id, pentru a îl putea obține cu
        // getScope() în a doua trecere.
        var id   = formal.id;
        var type = formal.type;

        var symbol = new IdSymbol(id.getToken().getText());

        // Verificăm dacă parametrul deja există în scope-ul curent.
        if (! currentScope.add(symbol)) {
            ASTVisitor.error(id.getToken(),
                  id.getToken().getText() + " redefined");
            return null;
        }

        id.setSymbol(symbol);
        id.setScope(currentScope);

        // TODO 1: Reținem informația de tip în simbolul nou creat.
        // Căutăm tipul variabilei.
        var formalType = currentScope.lookup(type.getToken().getText());

        // Semnalăm eroare dacă nu există.
        if (!(formalType instanceof TypeSymbol)) {
            ASTVisitor.error(
                    type.getToken(),
                    "type " + type.getToken().getText() + " undefined"
            );
            return null;
        }

        // Reținem informația de tip în cadrul simbolului aferent variabilei.
        symbol.setType((TypeSymbol)formalType);

        // Tipul unei definiții ca instrucțiune în sine nu este relevant.
        return null;
    }

    @Override
    public Void visit(Call call) {
        var id = call.id;
        for (var arg: call.args) {
            arg.accept(this);
        }
        id.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        assign.id.accept(this);
        assign.expr.accept(this);
        return null;
    }

    @Override
    public Void visit(If iff) {
        iff.cond.accept(this);
        iff.thenBranch.accept(this);
        iff.elseBranch.accept(this);
        return null;
    }

    @Override
    public Void visit(Type type) {
        return null;
    }

    // Operații aritmetice.
    @Override
    public Void visit(UnaryMinus uMinus) {
        uMinus.expr.accept(this);
        return null;
    }

    @Override
    public Void visit(Div div) {
        div.left.accept(this);
        div.right.accept(this);
        return null;
    }

    @Override
    public Void visit(Mult mult) {
        mult.left.accept(this);
        mult.right.accept(this);
        return null;
    }

    @Override
    public Void visit(Plus plus) {
        plus.left.accept(this);
        plus.right.accept(this);
        return null;
    }

    @Override
    public Void visit(Minus minus) {
        minus.left.accept(this);
        minus.right.accept(this);
        return null;
    }

    @Override
    public Void visit(Relational relational) {
        relational.left.accept(this);
        relational.right.accept(this);
        return null;
    }

    // Tipurile de bază
    @Override
    public Void visit(Int intt) {
        return null;
    }

    @Override
    public Void visit(Bool bool) {
        return null;
    }

    @Override
    public Void visit(FloatNum floatNum) {
        return null;
    }

}