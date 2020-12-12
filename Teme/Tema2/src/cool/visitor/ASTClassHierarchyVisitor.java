package cool.visitor;

import cool.AST.*;
import cool.scopes.Scope;
import cool.scopes.SymbolTable;
import cool.symbols.MethodSymbol;
import cool.symbols.Symbol;
import cool.symbols.TypeSymbol;

public class ASTClassHierarchyVisitor implements ASTVisitor<Void> {
	Scope scope;

	@Override
	public Void visit(ASTProgramNode programNode) {
		programNode.getClasses().forEach(this::visit);
		return null;
	}

	private void setHierarchy(ASTClassNode node) {
		var type = node.getType();
		Symbol parentType;

		while (type != TypeSymbol.OBJECT) {
			parentType = SymbolTable.globals.lookup(type.getParentName());
			if (parentType == null) {
				SymbolTable.error(
						node.getContext(),
						node.getBaseName(),
						"Class " + node.getName().getText() + " has undefined parent " + type.getParentName()
				);
				return;
			}

			type.setParent((TypeSymbol)parentType);
			type = (TypeSymbol)type.getParent();

			if (type == node.getType()) {
				SymbolTable.error(
						node.getContext(),
						node.getName(),
						"Inheritance cycle for class " + node.getName().getText()
				);
				return;
			}
		}
	}

	@Override
	public Void visit(ASTClassNode classNode) {
		if (classNode.getType() == null) {
			return null;
		}

		setHierarchy(classNode);

		scope = classNode.getType();
		classNode.getContent().forEach(node -> node.accept(this));

		return null;
	}

	@Override
	public Void visit(ASTFormalNode formalNode) {
		var formalId = formalNode.getIdSymbol();
		if (formalId == null) {
			return null;
		}

		var methodScope = (MethodSymbol)scope;
		var classScope = (TypeSymbol)methodScope.getParent();

		var formalType = SymbolTable.globals.lookup(formalNode.getType().getText());
		if (formalType == null) {
			SymbolTable.error(
					formalNode.getContext(),
					formalNode.getType(),
					"Method " + methodScope.getName() + " of class " + classScope.getName()
							+ " has formal parameter " + formalNode.getName().getText() + " with undefined type "
							+ formalNode.getType().getText()
			);
			return null;
		}

		formalId.setType((TypeSymbol)formalType);

		return null;
	}

	@Override
	public Void visit(ASTMethodNode methodNode) {
		var methodSymbol = methodNode.getMethodSymbol();
		if (methodSymbol == null) {
			return null;
		}

		var methodName = methodNode.getName().getText();
		var retType = methodNode.getRetType().getText();

		var retSymbol = SymbolTable.globals.lookup(retType);
		if (retSymbol == null) {
			SymbolTable.error(
					methodNode.getContext(),
					methodNode.getRetType(),
					"Class " + scope + " has method " + methodName + " with undefined return type " + retType
			);
			return null;
		}
		methodSymbol.setReturnType((TypeSymbol)retSymbol);

		scope = methodSymbol;
		methodNode.getParams().forEach(node -> node.accept(this));
		scope = methodSymbol.getParent();

		return null;
	}

	@Override
	public Void visit(ASTAttributeNode attributeNode) {
		var symbol = attributeNode.getIdSymbol();
		if (symbol == null) {
			return null;
		}

		var attribName = attributeNode.getName().getText();
		var typeName = attributeNode.getType().getText();

		var parentScope = scope.getParent();
		if (parentScope.lookup(attribName) != null) {
			SymbolTable.error(
					attributeNode.getContext(),
					attributeNode.getName(),
					"Class " + ((TypeSymbol)scope).getName()
							+ " redefines inherited attribute " + attribName
			);
			attributeNode.setIdSymbol(null);

			return null;
		}

		var typeSymbol = SymbolTable.globals.lookup(typeName);
		if (typeSymbol == null) {
			SymbolTable.error(
					attributeNode.getContext(),
					attributeNode.getType(),
					"Class " + ((TypeSymbol)scope).getName() + " has attribute " + attribName
							+ " with undefined type " + typeName
			);
			attributeNode.setIdSymbol(null);

			return null;
		}
		attributeNode.getIdSymbol().setType((TypeSymbol)typeSymbol);

		return null;
	}

	@Override
	public Void visit(ASTLocalVarNode localVarNode) {
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
		return null;
	}

	@Override
	public Void visit(ASTUnaryOperatorNode unaryOpNode) {
		return null;
	}

	@Override
	public Void visit(ASTIfNode ifNode) {
		return null;
	}

	@Override
	public Void visit(ASTWhileNode whileNode) {
		return null;
	}

	@Override
	public Void visit(ASTLetNode letNode) {
		return null;
	}

	@Override
	public Void visit(ASTCaseBranchNode caseBranchNode) {
		return null;
	}

	@Override
	public Void visit(ASTCaseNode caseNode) {
		return null;
	}

	@Override
	public Void visit(ASTBlockNode blockNode) {
		return null;
	}
}
