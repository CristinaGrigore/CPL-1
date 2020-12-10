package cool.visitor;

import cool.AST.*;
import cool.parser.CoolParser;
import cool.scopes.Scope;
import cool.scopes.SymbolTable;
import cool.symbols.TypeSymbol;

public class ASTResolutionVisitor implements ASTVisitor<TypeSymbol> {
	Scope scope;

	@Override
	public TypeSymbol visit(ASTProgramNode programNode) {
		programNode.getClasses().forEach(this::visit);
		return null;
	}

	@Override
	public TypeSymbol visit(ASTClassNode classNode) {
		if (classNode.getType() == null) {
			return null;
		}

		scope = classNode.getType();
		classNode.getContent().forEach(node -> node.accept(this));

		return null;
	}

	@Override
	public TypeSymbol visit(ASTFormalNode formalNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTMethodNode methodNode) {
		var methodSymbol = methodNode.getMethodSymbol();
		if (methodSymbol == null) {
			return null;
		}

		var methodName = methodNode.getName().getText();
		var classScope = (TypeSymbol)scope;
		var retSymbol = methodSymbol.getReturnType();

		var overriddenMethod = ((TypeSymbol)classScope.getParent()).lookupMethod(methodName);
		if (overriddenMethod != null) {
			var overriddenType = overriddenMethod.getReturnType();
			if (retSymbol != overriddenType) {
				SymbolTable.error(
						methodNode.getContext(),
						methodNode.getRetType(),
						"Class " + classScope.getName() + " overrides method " + methodName
								+ " but changes return type from " + overriddenType + " to " + retSymbol
				);
				return null;
			}

			var formals = methodNode.getMethodSymbol().getFormals();
			var overriddenFormals = overriddenMethod.getFormals();
			if (formals.size() != overriddenFormals.size()) {
				SymbolTable.error(
						methodNode.getContext(),
						methodNode.getName(),
						"Class " + classScope.getName() + " overrides method " + methodName
								+ " with different number of formal parameters"
				);
				return null;
			}

			TypeSymbol formalSymb, overriddenSymb;
			for (int i = 0; i != formals.size(); ++i) {
				formalSymb = formals.get(i).getType();
				overriddenSymb = overriddenFormals.get(i).getType();

				if (formalSymb == null || overriddenSymb == null) {
					continue;
				}

				if (!formalSymb.inherits(overriddenSymb)) {
					SymbolTable.error(
							methodNode.getContext(),
							((CoolParser.MethodDefContext)methodNode.getContext()).params.get(i).stop,
							"Class " + classScope.getName() + " overrides method " + methodName
									+ " but changes type of formal parameter " + formals.get(i).getName() + " from "
									+ overriddenSymb.getName() + " to " + formalSymb.getName()
					);
					return null;
				}
			}
		}

//		scope = methodSymbol;

		// TODO: o sa fie doar NULL
//		var body = methodNode.getBody()
//				.stream()
//				.map(node -> node.accept(this))
//				.collect(Collectors.toList());
//		if (body.get(body.size() - 1) != retSymbol) {
//			SymbolTable.error(
//					methodNode.getContext(),
//					methodNode.getBody().get(body.size() - 1).getContext().start,
//					"Type < " + body.get(body.size() - 1) + " of the body of method " + methodName
//							+ " is incompatible with declared return type " + retSymbol
//			);
//		}

//		scope = classScope;

		return null;
	}

	@Override
	public TypeSymbol visit(ASTAttributeNode attributeNode) {
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
			return null;
		}

		attributeNode.getIdSymbol().setType((TypeSymbol)typeSymbol);

		var value = attributeNode.getValue();
		if (value != null) {
			value.accept(this);
		}

		return null;
	}

	@Override
	public TypeSymbol visit(ASTLocalVarNode localVarNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTIntNode intNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTIdNode idNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTBoolNode boolNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTStringNode stringNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTAssignNode assignNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTNewNode newNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTDispatchNode dispatchNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTBinaryOperatorNode binaryOpNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTUnaryOperatorNode unaryOpNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTIfNode ifNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTWhileNode whileNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTLetNode letNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTCaseBranchNode caseBranchNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTCaseNode caseNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTBlockNode blockNode) {
		return null;
	}
}
