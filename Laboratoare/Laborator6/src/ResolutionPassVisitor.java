import org.antlr.v4.runtime.Token;

import java.util.stream.Collectors;

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
            var type = getAssignType(varType, initType);
            if (type == null) {
                ASTVisitor.error(
                        varDef.initValue.getToken(),
                        "Type of initialization expression does not match variable type"
                );
            }
        }
        
        return null;
    }
    
    @Override
    public TypeSymbol visit(FuncDef funcDef) {
        var returnType = funcDef.id.getSymbol().getType();
        var bodyType = funcDef.body.accept(this);
        
        // TODO 5: Verificăm dacă tipul de retur declarat este compatibil
        // cu cel al corpului.
        var type = getAssignType(returnType, bodyType);
        if (type == null) {
            ASTVisitor.error(
                    funcDef.body.getToken(),
                    "Return type does not match body type"
            );
        }

        return type;
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
        if (formals.size() != call.args.size()) {
            ASTVisitor.error(
                    id.getToken(),
                    id.getToken().getText() + " applied to wrong number of arguments"
            );
            return null;
        }

        var argsTypes = call.args.stream().map(arg -> arg.accept((this))).collect(Collectors.toList());

        int i = 0;
        for (var val : formals.values()) {
            if (getAssignType(((IdSymbol)val).getType(), argsTypes.get(i)) == null) {
                ASTVisitor.error(
                        call.args.get(i).getToken(),
                        "Argument " + (i + 1) + " of " + call.getToken().getText() + " has wrong type"
                );
                return null;
            }
            ++i;
        }
        
        return functionSymbol.getType();
    }
    
    @Override
    public TypeSymbol visit(Assign assign) {
        var idType   = assign.id.accept(this);
        var exprType = assign.expr.accept(this);
        
        // TODO 5: Verificăm dacă expresia cu care se realizează atribuirea
        // are tipul potrivit cu cel declarat pentru variabilă.
        if (idType == null || exprType == null) {
            return null;
        }

        var assignType = getAssignType(idType, exprType);
        if (assignType == null) {
            ASTVisitor.error(assign.expr.getToken(), "Assignment with incompatible types");
        }
        
        return assignType;
    }

    private TypeSymbol getAssignType(TypeSymbol idType, TypeSymbol exprType) {
        if (idType == null || exprType == null) {
            return  null;
        }

        if (idType == exprType) {
            return  idType;
        }

        if (idType == TypeSymbol.FLOAT && exprType == TypeSymbol.INT) {
            return TypeSymbol.FLOAT;
        }

        return null;
    }

    @Override
    public TypeSymbol visit(If iff) {        
        var condType = iff.cond.accept(this);
        var thenType = iff.thenBranch.accept(this);
        var elseType = iff.elseBranch.accept(this);
        
        // TODO 4: Verificați tipurile celor 3 componente, afișați eroare
        // dacă este cazul, și precizați tipul expresiei.
        if (condType != TypeSymbol.BOOL) {
            ASTVisitor.error(iff.cond.getToken(), "Condition of if expression has type other than Bool");
        }

        var exprType = getCompatibleTypes(thenType, elseType);
        if (exprType == null) {
            ASTVisitor.error(iff.getToken(), "Branches of if expression have incompatible types");
        }

        return exprType;
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
        if (exprType != TypeSymbol.INT && exprType != TypeSymbol.FLOAT) {
            ASTVisitor.error(
                    uMinus.getToken(),
                    "Operands of " + uMinus.getToken().getText() + " have incompatible types"
            );
            return null;
        }

        return exprType;
    }

    @Override
    public TypeSymbol visit(Div div) {
        // TODO 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.
        return visitOperation(div.left, div.right, div.token);
    }

    @Override
    public TypeSymbol visit(Mult mult) {
        // TODO 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.
        return visitOperation(mult.left, mult.right, mult.token);
    }

    @Override
    public TypeSymbol visit(Plus plus) {
        // TODO 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.
        return visitOperation(plus.left, plus.right, plus.token);
    }

    @Override
    public TypeSymbol visit(Minus minus) {
        // TODO 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.
        return visitOperation(minus.left, minus.right, minus.token);
    }

    @Override
    public TypeSymbol visit(Relational relational) {
        // TODO 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.
        // Puteți afla felul operației din relational.getToken().getType(),
        // pe care îl puteți compara cu CPLangLexer.EQUAL etc.
        var leftSymbol = relational.left.accept(this);
        var rightSymbol = relational.right.accept(this);

        if (relational.getToken().getType() == CPLangLexer.EQUAL
                && leftSymbol == TypeSymbol.BOOL && rightSymbol == TypeSymbol.BOOL
        ) {
            return TypeSymbol.BOOL;
        }

        if (getResultType(leftSymbol, rightSymbol) == null) {
            ASTVisitor.error(
                    relational.getToken(),
                    "Operands of " + relational.getToken().getText() + " have incompatible types"
            );
            return null;
        }

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

    private TypeSymbol visitOperation(Expression left, Expression right, Token op) {
        var leftSymbol = left.accept(this);
        var rightSymbol = right.accept(this);

        var type = getResultType(leftSymbol, rightSymbol);
        if (type == null) {
            ASTVisitor.error(
                    op,
                    "Operands of " + op.getText() + " have incompatible types"
            );
            return null;
        }

        return type;
    }

    private TypeSymbol getCompatibleTypes(TypeSymbol left, TypeSymbol right) {
        if (left == null || right == null) {
            return null;
        }

        if (left == right) {
            return left;
        }

        if (left == TypeSymbol.FLOAT || right == TypeSymbol.FLOAT) {
            return TypeSymbol.FLOAT;
        }

        return null;
    }

    private TypeSymbol getResultType(TypeSymbol left, TypeSymbol right) {
        if (left == TypeSymbol.BOOL || right == TypeSymbol.BOOL || left == null || right == null) {
            return null;
        }

        if (left == TypeSymbol.FLOAT || right == TypeSymbol.FLOAT) {
            return TypeSymbol.FLOAT;
        }

        return TypeSymbol.INT;
    }
};