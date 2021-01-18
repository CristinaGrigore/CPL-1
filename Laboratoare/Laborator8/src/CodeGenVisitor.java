import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class CodeGenVisitor implements ASTVisitor<ST>{
	static STGroupFile templates = new STGroupFile("cgen.stg");
	static long CNT = 1;
	
	ST mainSection;	 // filled directly (through visitor returns)
	ST dataSection;  // filled collaterally ("global" access)
	ST funcSection;  // filled collaterally ("global" access)

	/* 
	 * Plain numbers
	 * TODO 1:
	 */
    @Override
    public ST visit(Int val) {
	    var value = Integer.parseInt(val.getToken().getText());

	    return templates.getInstanceOf("literal")
			    .add("hi", value >> 16)
			    .add("lo", value & 0x0000ffff);
    }
    
    @Override
    public ST visit(FloatNum val) {
	    return templates.getInstanceOf("floatLiteral")
			    .add("value", val.getToken().getText());
    }

    @Override
    public ST visit(Bool val) {
    	var value = val.getToken().getText().equals("true") ? "1" : "0";
        return templates.getInstanceOf("literal")
		        .add("hi", 0)
		        .add("lo", value);
    }
    
    /* 
     * Unary operations
     * TODO 1:
     */
	@Override
	public ST visit(UnaryMinus uMinus) {
		var expr = uMinus.expr.accept(this);

		if (expr.getName().equals("/literal")) {
			return templates.getInstanceOf("neg").add("expr", expr);
		}

		return templates.getInstanceOf("floatNeg").add("expr", expr);
	}
    
	/* 
	 * Binary operations
	 * TODO 2:
	 */
    @Override
    public ST visit(Plus expr) {
    	return templates.getInstanceOf("binaryOp")
				.add("e1", expr.left.accept(this))
    		    .add("e2",  expr.right.accept(this))
    		    .add("dStr", expr.debugStr)
			    .add("op", "add");
    }
    
    @Override
    public ST visit(Minus expr) {
    	return templates.getInstanceOf("binaryOp")
			    .add("e1", expr.left.accept(this))
			    .add("e2",  expr.right.accept(this))
			    .add("dStr", expr.debugStr)
			    .add("op", "sub");
    }
    
    @Override
    public ST visit(Mult expr) {
	    return templates.getInstanceOf("binaryOp")
			    .add("e1", expr.left.accept(this))
			    .add("e2",  expr.right.accept(this))
			    .add("dStr", expr.debugStr)
			    .add("op", "mul");
    }
    
    @Override
    public ST visit(Div expr) {
	    return templates.getInstanceOf("binaryOp")
			    .add("e1", expr.left.accept(this))
			    .add("e2",  expr.right.accept(this))
			    .add("dStr", expr.debugStr)
			    .add("op", "div");
    }
	
	@Override
	public ST visit(Relational expr) {
    	String op = switch (expr.getToken().getText()) {
		    case "==" -> "seq";
		    case "<" -> "slt";
		    default -> "sle";
	    };

		return templates.getInstanceOf("binaryOp")
				.add("e1", expr.left.accept(this))
				.add("e2",  expr.right.accept(this))
				.add("dStr", expr.debugStr)
				.add("op", op);
	}

    /*
     * Control structures
     * TODO 3:
     */
    @Override
	public ST visit(If iff) {
		return templates.getInstanceOf("if")
				.add("e1", iff.cond.accept(this))
				.add("e2", iff.thenBranch.accept(this))
				.add("e3", iff.elseBranch.accept(this))
				.add("l_skip", "skip_" + CNT)
				.add("l_else", "else_" + CNT++);
	}

	@Override
	public ST visit(Call call) {
    	var params = call.args
			    .stream()
			    .map(arg -> templates.getInstanceOf("callParam")
					    .add("expr", arg.accept(this)))
			    .collect(Collectors.toList());

    	return templates.getInstanceOf("call")
			    .add("f", call.id.getSymbol().getName())
			    .add("params", params);
	}

    /*
     * Definitions & assignments
     * TODO 4&5:
     */
	@Override
	public ST visit(Assign assign) {
		// TODO 4: generare cod pentru main()
		return templates.getInstanceOf("assign")
				.add("name", assign.id.token.getText())
				.add("value", assign.expr.accept(this));
	}

	@Override
	public ST visit(VarDef varDef) {
		// TODO 4: generare cod pentru main() și etichetă în .data
		var varName = varDef.id.token.getText();
		var varType = varDef.type.token.getText();

		var st = templates.getInstanceOf("varDef").add("name", varName);

		var typeName = varType.equals("Float") ? "float" : "word";
		st.add("type", typeName);

		var initValue = varType.equals("Float") ? "0.0" : "0";
		st.add("value", initValue);

		dataSection.add("e", st);

		if (varDef.initValue != null) {
			return templates.getInstanceOf("assign")
					.add("name", varName)
					.add("value", varDef.initValue.accept(this));
		}

		return null;
	}

	@Override
	public ST visit(FuncDef funcDef) {
		// TODO 5: generare cod pentru funcSection. Fără cod în main()!
		var params = funcDef.formals
				.stream()
				.map(formal -> formal.accept(this))
				.collect(Collectors.toList());
		Collections.reverse(params);
		var st = templates.getInstanceOf("funcDef")
				.add("name", funcDef.id.getSymbol().getName())
				.add("params", params)
				.add("body", funcDef.body.accept(this))
				.add("offset", funcDef.formals.size() * 4 + 8);

		funcSection.add("e", st);

		return null;
	}

	/*
	 * META
	 */
	@Override
	public ST visit(Id id) {
		// TODO 5
		if (id.getSymbol().isFormal()) {
			return templates.getInstanceOf("localVar").add("offset", id.getSymbol().getOffset());
		}

		return templates.getInstanceOf("id").add("name", id.getSymbol().getName());
	}

	@Override
	public ST visit(Formal formal) {
		// TODO 5
		formal.id.getSymbol().setFormal();
		return null;
	}
	
	@Override
	public ST visit(Type type) {
		return null;
	}

	@Override
	public ST visit(Program program) {
		dataSection = templates.getInstanceOf("sequenceSpaced");
		funcSection = templates.getInstanceOf("sequenceSpaced");
		mainSection = templates.getInstanceOf("sequence");
		
		for (ASTNode e : program.stmts)
			mainSection.add("e", e.accept(this));
		
		//assembly-ing it all together. HA! get it?
		var programST = templates.getInstanceOf("program");
		programST.add("data", dataSection);
		programST.add("textFuncs", funcSection);
		programST.add("textMain", mainSection);
		
		return programST;
	}

}
