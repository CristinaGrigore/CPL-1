import org.antlr.v4.runtime.Token;

public class ResolutionPassVisitor implements ASTVisitor<TypeSymbol> {     
    @Override
    public TypeSymbol visit(Program prog) {
        for (var stmt: prog.stmts) {
            stmt.accept(this);
        }
        return null;
    }
    
    @Override
    public TypeSymbol visit(Id id) {
        // Verificăm dacă într-adevăr avem de-a face cu o variabilă
        // sau cu o funcție, al doilea caz constituind eroare.
        // Puteți folosi instanceof.
        var symbol = id.getScope().lookup(id.getToken().getText());

        if (symbol instanceof FunctionSymbol) {
            ASTVisitor.error(id.getToken(),
                  id.getToken().getText() + " is not a variable");
            return null;
        }

        // TODO 2: Întoarcem informația de tip salvată deja în simbol încă de la
        // definirea variabilei.
        return ((IdSymbol)symbol).getType();
    }
    
    @Override
    public TypeSymbol visit(VarDef varDef) {
        if (varDef.initValue != null) {
            var varType  = varDef.id.getSymbol().getType();
            var initType = varDef.initValue.accept(this);
            
            // TODO 5: Verificăm dacă expresia de inițializare are tipul potrivit
            // cu cel declarat pentru variabilă.
            
            if (varType == null || initType == null)
                return null;

            if (varType != initType)
                ASTVisitor.error(varDef.initValue.getToken(),
                        "Type of initilization expression does not match variable type");
        }
        
        return null;
    }
    
    @Override
    public TypeSymbol visit(FuncDef funcDef) {
        var returnType = funcDef.id.getSymbol().getType();
        var bodyType = funcDef.body.accept(this);
        
        // TODO 5: Verificăm dacă tipul de retur declarat este compatibil
        // cu cel al corpului.
        
        if (returnType == null || bodyType == null)
            return null;
        
        if (returnType != bodyType)
            ASTVisitor.error(funcDef.body.getToken(),
                    "Return type does not match body type");
        
        return null;
    }

    @Override
    public TypeSymbol visit(Call call) {
        // Verificăm dacă funcția există în scope. Nu am putut face
        // asta în prima trecere din cauza a forward references.
        //
        // De asemenea, verificăm că Id-ul pe care se face apelul de funcție
        // este, într-adevăr, o funcție și nu o variabilă.
        //
        // Hint: pentru a obține scope-ul, putem folosi call.id.getScope(),
        // setat la trecerea anterioară.
        var id = call.id;
        var symbol = id.getScope().lookup(id.getToken().getText());

        if (symbol == null) {
            ASTVisitor.error(id.getToken(),
                  id.getToken().getText() + " function undefined");
            return null;
        }

        if (!(symbol instanceof FunctionSymbol)) {
            ASTVisitor.error(id.getToken(),
                  id.getToken().getText() + " is not a function");
            return null;
        }
        
        var functionSymbol = (FunctionSymbol)symbol;
        id.setSymbol(functionSymbol);
        
        // TODO 6: Verificați dacă numărul parametrilor actuali coincide
        // cu cel al parametrilor formali, și că tipurile sunt compatibile.
        
        var formals = functionSymbol.getFormals();
        
        // Verificăm că numărul de parametri formali și actuali coincid.
        if (formals.size() != call.args.size()) {
            ASTVisitor.error(call.getToken(),
                    call.id.getToken().getText() +
                    " applied to wrong number of arguments");
        // Verificăm că tipurile parametrilor formali și actuali coincid.
        } else {
            var index = 0;
            var formalIterator = formals.entrySet().iterator();
            var actualIterator = call.args.iterator();
            
            while (formalIterator.hasNext()) {
                index++;
                var formal = formalIterator.next();
                var actual = actualIterator.next();
                
                var formalType = ((IdSymbol)formal.getValue()).getType();
                var actualType = actual.accept(this);
                
                if (formalType == null || actualType == null)
                    continue;
                
                if (formalType != actualType)
                    ASTVisitor.error(actual.getToken(),
                            "Argument " + index +
                            " of " + call.id.getToken().getText() +
                            " has wrong type");
            }
        }
        
        return functionSymbol.getType();
    }   
    
    @Override
    public TypeSymbol visit(Assign assign) {
        var idType   = assign.id.accept(this);
        var exprType = assign.expr.accept(this);
        
        // TODO 5: Verificăm dacă expresia cu care se realizează atribuirea
        // are tipul potrivit cu cel declarat pentru variabilă.
        
        if (idType == null || exprType == null)
            return null;
        
        if (idType != exprType) {
            ASTVisitor.error(assign.expr.getToken(),
                    "Assignment with incompatible types");
            return null;
        }
        
        return exprType;
    }

    @Override
    public TypeSymbol visit(If iff) {        
        var condType = iff.cond.accept(this);
        var thenType = iff.thenBranch.accept(this);
        var elseType = iff.elseBranch.accept(this);
        
        // TODO 4: Verificați tipurile celor 3 componente, afișați eroare
        // dacă este cazul, și precizați tipul expresiei.
        
        if (condType != null && condType != TypeSymbol.BOOL)
            ASTVisitor.error(iff.cond.getToken(),
                    "Condition of if expression has type other than Bool");
        
        if (thenType != null && elseType != null && thenType != elseType) {
            ASTVisitor.error(iff.getToken(),
                    "Branches of if expression have incompatible types");
            return null;
        }

        return thenType;
    }

    @Override
    public TypeSymbol visit(Type type) {
        return null;
    }

    @Override
    public TypeSymbol visit(Formal formal) {
        return formal.id.getSymbol().getType();
    }

    // Operații aritmetice.
    @Override
    public TypeSymbol visit(UnaryMinus uMinus) {
        var exprType = uMinus.expr.accept(this);
        
        // TODO 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.
        
        if (exprType == TypeSymbol.BOOL)
            ASTVisitor.error(uMinus.getToken(),
                    "Unary minus applied to Bool");
        
        return exprType;
    }

    @Override
    public TypeSymbol visit(Div div) {
        // TODO 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.
        return checkBinaryOpTypes(div.getToken(), div.left, div.right);
    }

    @Override
    public TypeSymbol visit(Mult mult) {
        // TODO 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.
        return checkBinaryOpTypes(mult.getToken(), mult.left, mult.right);
    }

    @Override
    public TypeSymbol visit(Plus plus) {
        // TODO 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.
        return checkBinaryOpTypes(plus.getToken(), plus.left, plus.right);
    }

    @Override
    public TypeSymbol visit(Minus minus) {
        // TODO 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.
        return checkBinaryOpTypes(minus.getToken(), minus.left, minus.right);
    }

    @Override
    public TypeSymbol visit(Relational relational) {
        // TODO 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.
        // Puteți afla felul operației din relational.getToken().getType(),
        // pe care îl puteți compara cu CPLangLexer.EQUAL etc.
        checkBinaryOpTypes(relational.getToken(), relational.left, relational.right);
        return TypeSymbol.BOOL;
    }

    // Tipurile de bază
    @Override
    public TypeSymbol visit(Int intt) {
        // TODO 2: Întoarcem tipul corect.
        return TypeSymbol.INT;
    }

    @Override
    public TypeSymbol visit(Bool bool) {
        // TODO 2: Întoarcem tipul corect.
        return TypeSymbol.BOOL;
    }

    @Override
    public TypeSymbol visit(FloatNum floatNum) {
        // TODO 2: Întoarcem tipul corect.
        return TypeSymbol.FLOAT;
    }
    
    TypeSymbol checkBinaryOpTypes(Token token, Expression e1, Expression e2) {
        var type1 = e1.accept(this);
        var type2 = e2.accept(this);
        
        if (type1 == null || type2 == null)
            return null;
        
        if (type1 == TypeSymbol.INT && type2 == TypeSymbol.INT ||
            type1 == TypeSymbol.FLOAT && type2 == TypeSymbol.FLOAT)
            return type1;
        
        if (token.getType() == CPLangLexer.EQUAL &&
            type1 == TypeSymbol.BOOL && type2 == TypeSymbol.BOOL)
            return type1;
        
        ASTVisitor.error(token,
                "Operands of " + token.getText() + " have incompatible types");
        
        return null;
    }
    
};