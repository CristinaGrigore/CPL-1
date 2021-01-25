package cool.visitor;

import cool.AST.*;
import cool.scopes.Scope;
import cool.scopes.SymbolTable;
import cool.symbols.IdSymbol;
import cool.symbols.LetSymbol;
import cool.symbols.MethodSymbol;
import cool.symbols.TypeSymbol;

import java.util.HashSet;

public class ASTDefinitionVisitor implements ASTVisitor<Void> {
	private Scope scope;
	private final HashSet<String> illegalParents;

	public ASTDefinitionVisitor() {
		illegalParents = new HashSet<>();
		illegalParents.add(TypeSymbol.INT.getName());
		illegalParents.add(TypeSymbol.BOOL.getName());
		illegalParents.add(TypeSymbol.STRING.getName());
		illegalParents.add("SELF_TYPE");
	}

	@Override
	public Void visit(ASTProgramNode programNode) {
		programNode.getClasses().forEach(this::visit);
		return null;
	}

	@Override
	public Void visit(ASTClassNode classNode) {
		var className = classNode.getName().getText();
		if (className.equals("SELF_TYPE")) {
			SymbolTable.error(
					classNode.getContext(),
					classNode.getName(),
					"Class has illegal name " + className
			);
			return null;
		}

		var parentToken = classNode.getBaseName();
		var parentName = parentToken == null ? "Object" : parentToken.getText();

		var type = new TypeSymbol(className, parentName);
		if (!SymbolTable.globals.add(type)) {
			SymbolTable.error(
					classNode.getContext(),
					classNode.getName(),
					"Class " + className + " is redefined"
			);
			return null;
		}
		if (illegalParents.contains(parentName)) {
			SymbolTable.error(
					classNode.getContext(),
					classNode.getBaseName(),
					"Class " + className + " has illegal parent " + parentName
			);
			return null;
		}

		classNode.setType(type);

		scope = type;
		classNode.getContent().forEach(node -> node.accept(this));

		return null;
	}

	@Override
	public Void visit(ASTFormalNode formalNode) {
		var formalName = formalNode.getName().getText();
		var methodScope = (MethodSymbol)scope;
		var classScope = (TypeSymbol)scope.getParent();

		if (formalName.equals("self")) {
			SymbolTable.error(
					formalNode.getContext(),
					formalNode.getName(),
					"Method " + methodScope.getName() + " of class "  + classScope.getName()
							+ " has formal parameter with illegal name " + formalName
			);
			return null;
		}

		var formalType = formalNode.getType().getText();
		if (formalType.equals("SELF_TYPE")) {
			SymbolTable.error(
					formalNode.getContext(),
					formalNode.getType(),
					"Method " + methodScope.getName() + " of class "  + classScope.getName()
							+ " has formal parameter " + formalName + " with illegal type " + formalType
			);
			return null;
		}

		var formalSymbol = new IdSymbol(formalName);
		if (!scope.add(formalSymbol)) {
			SymbolTable.error(
					formalNode.getContext(),
					formalNode.getName(),
					"Method " + methodScope + " of class " + classScope
							+ " redefines formal parameter " + formalName
			);
			return null;
		}

		formalNode.setIdSymbol(formalSymbol);

		return null;
	}

	@Override
	public Void visit(ASTMethodNode methodNode) {
		var methodName = methodNode.getName().getText();
		var classScope = (TypeSymbol)scope;
		var methodSymbol = new MethodSymbol(methodName);
		methodSymbol.setParent(classScope);

		if (!classScope.addMethod(methodSymbol)) {
			SymbolTable.error(
					methodNode.getContext(),
					methodNode.getName(),
					"Class " + classScope.getName()  + " redefines method " + methodName
			);
			return null;
		}

		methodNode.setMethodSymbol(methodSymbol);

		scope = methodSymbol;
		methodNode.getParams().forEach(node -> node.accept(this));
		methodNode.getBody().accept(this);
		scope = scope.getParent();

		int i = 12;
		for(var formal : methodNode.getParams()) {
			formal.getIdSymbol().setOffset(i);
			i += 4;
		}

		return null;
	}

	@Override
	public Void visit(ASTAttributeNode attributeNode) {
		var attribName = attributeNode.getName().getText();

		if (attribName.equals("self")) {
			SymbolTable.error(
					attributeNode.getContext(),
					attributeNode.getName(),
					"Class " + ((TypeSymbol)scope).getName()  + " has attribute with illegal name " + attribName
			);
			return null;
		}

		var symbol = new IdSymbol(attribName);
		if (!scope.add(symbol)) {
			SymbolTable.error(
					attributeNode.getContext(),
					attributeNode.getName(),
					"Class " + ((TypeSymbol)scope).getName()  + " redefines attribute " + attribName
			);
			return null;
		}

		attributeNode.setIdSymbol(symbol);

		var value = attributeNode.getValue();
		if (value != null) {
			value.accept(this);
		}

		return null;
	}

	@Override
	public Void visit(ASTLocalVarNode localVarNode) {
		var varName = localVarNode.getName().getText();
		if (varName.equals("self")) {
			SymbolTable.error(
					localVarNode.getContext(),
					localVarNode.getName(),
					"Let variable has illegal name " + varName
			);
			return null;
		}

		var varSymb = new IdSymbol(varName);
		localVarNode.setIdSymbol(varSymb);

		var value = localVarNode.getValue();
		if (value != null) {
			value.accept(this);
		}

		return null;
	}

	@Override
	public Void visit(ASTIntNode intNode) {
		return null;
	}

	@Override
	public Void visit(ASTIdNode idNode) {
		return null;
	}

	@Override
	public Void visit(ASTBoolNode boolNode) {
		return null;
	}

	@Override
	public Void visit(ASTStringNode stringNode) {
		return null;
	}

	@Override
	public Void visit(ASTAssignNode assignNode) {
		assignNode.getValue().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTNewNode newNode) {
		return null;
	}

	@Override
	public Void visit(ASTDispatchNode dispatchNode) {
		return null;
	}

	@Override
	public Void visit(ASTBinaryOperatorNode binaryOpNode) {
		binaryOpNode.getLeftOp().accept(this);
		binaryOpNode.getRightOp().accept(this);

		return null;
	}

	@Override
	public Void visit(ASTUnaryOperatorNode unaryOpNode) {
		unaryOpNode.getOp().accept(this);

		return null;
	}

	@Override
	public Void visit(ASTIfNode ifNode) {
		ifNode.getThenBranch().accept(this);
		ifNode.getElseBranch().accept(this);

		return null;
	}

	@Override
	public Void visit(ASTWhileNode whileNode) {
		whileNode.getBody().accept(this);
		return null;
	}

	@Override
	public Void visit(ASTLetNode letNode) {
		letNode.setLetSymbol(new LetSymbol(scope));

		scope = letNode.getLetSymbol();
		letNode.getLocals().forEach(local -> local.accept(this));
		letNode.getBody().accept(this);
		scope = scope.getParent();

		return null;
	}

	@Override
	public Void visit(ASTCaseBranchNode caseBranchNode) {
		var idName = caseBranchNode.getId().getText();
		if (idName.equals("self")) {
			SymbolTable.error(
					caseBranchNode.getContext(),
					caseBranchNode.getId(),
					"Case variable has illegal name " + idName
			);
			return null;
		}

		var typeName = caseBranchNode.getType().getText();
		if (typeName.equals("SELF_TYPE")) {
			SymbolTable.error(
					caseBranchNode.getContext(),
					caseBranchNode.getType(),
					"Case variable " + idName + " has illegal type " + typeName
			);
			return null;
		}

		caseBranchNode.getBody().accept(this);

		return null;
	}

	@Override
	public Void visit(ASTCaseNode caseNode) {
		caseNode.getBranches().forEach(br -> br.accept(this));
		return null;
	}

	@Override
	public Void visit(ASTBlockNode blockNode) {
		blockNode.getExpressions().forEach(expr -> expr.accept(this));
		return null;
	}
}
