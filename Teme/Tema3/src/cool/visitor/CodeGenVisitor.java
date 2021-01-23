package cool.visitor;

import cool.AST.*;
import cool.scopes.SymbolTable;
import cool.symbols.TypeSymbol;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

public class CodeGenVisitor implements ASTVisitor<ST> {
	private final STGroupFile templates;
	private long ifCount;

	private ST consts;
	private final ST classNames;
	private final ST classObjs;
	private ST protObjs;
	private ST dispTables;
	private ST methods;

	public CodeGenVisitor() {
		templates = new STGroupFile("cgen.stg");
		ifCount = 1;

		classNames = templates.getInstanceOf("sequence");
		classNames.add("e", "\t.word str_const_CLASS_Object");
		classNames.add("e", "\t.word str_const_CLASS_Int");
		classNames.add("e", "\t.word str_const_CLASS_String");
		classNames.add("e", "\t.word str_const_CLASS_Bool");
		classNames.add("e", "\t.word str_const_CLASS_IO");

		classObjs = templates.getInstanceOf("sequence");
		classObjs.add("e", templates.getInstanceOf("protObj").add("class", "Object"));
		classObjs.add("e", templates.getInstanceOf("protObj").add("class", "Int"));
		classObjs.add("e", templates.getInstanceOf("protObj").add("class", "String"));
		classObjs.add("e", templates.getInstanceOf("protObj").add("class", "Bool"));
		classObjs.add("e", templates.getInstanceOf("protObj").add("class", "IO"));

		dispTables = templates.getInstanceOf("sequence");
		SymbolTable.globals.getSymbols()
				.values()
				.forEach(clss -> {
							if (clss != TypeSymbol.SELF_TYPE) {
								dispTables.add("e", ((TypeSymbol)clss).getDispTable(templates));
							}
						}
				);
	}

	@Override
	public ST visit(ASTProgramNode programNode) {
		ST program = templates.getInstanceOf("program");
		program.add("consts", "");
		program.add("classNames", classNames);
		program.add("classObjs", classObjs);
		program.add("protObjs", "");
		program.add("dispTables", dispTables);
		program.add("methods", "");

		return program;
	}

	@Override
	public ST visit(ASTClassNode classNode) {
		return null;
	}

	@Override
	public ST visit(ASTFormalNode formalNode) {
		return null;
	}

	@Override
	public ST visit(ASTMethodNode methodNode) {
		return null;
	}

	@Override
	public ST visit(ASTAttributeNode attributeNode) {
		return null;
	}

	@Override
	public ST visit(ASTLocalVarNode localVarNode) {
		return null;
	}

	@Override
	public ST visit(ASTIntNode intNode) {
		return null;
	}

	@Override
	public ST visit(ASTIdNode idNode) {
		return null;
	}

	@Override
	public ST visit(ASTBoolNode boolNode) {
		return null;
	}

	@Override
	public ST visit(ASTStringNode stringNode) {
		return null;
	}

	@Override
	public ST visit(ASTAssignNode assignNode) {
		return null;
	}

	@Override
	public ST visit(ASTNewNode newNode) {
		return null;
	}

	@Override
	public ST visit(ASTDispatchNode dispatchNode) {
		return null;
	}

	@Override
	public ST visit(ASTBinaryOperatorNode binaryOpNode) {
		return null;
	}

	@Override
	public ST visit(ASTUnaryOperatorNode unaryOpNode) {
		return null;
	}

	@Override
	public ST visit(ASTIfNode ifNode) {
		return null;
	}

	@Override
	public ST visit(ASTWhileNode whileNode) {
		return null;
	}

	@Override
	public ST visit(ASTLetNode letNode) {
		return null;
	}

	@Override
	public ST visit(ASTCaseBranchNode caseBranchNode) {
		return null;
	}

	@Override
	public ST visit(ASTCaseNode caseNode) {
		return null;
	}

	@Override
	public ST visit(ASTBlockNode blockNode) {
		return null;
	}
}
