package cool.visitor;

import cool.AST.*;
import cool.parser.CoolParser;
import cool.scopes.Scope;
import cool.scopes.SymbolTable;
import cool.symbols.IdSymbol;
import cool.symbols.Symbol;
import cool.symbols.TypeSymbol;

import java.util.Objects;

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

		scope = methodSymbol;
		var body = methodNode.getBody();
		var bodyType = methodNode.getBody().accept(this);

		// TODO: scoate != null cand implementezi expresii
		if (bodyType != null && bodyType != retSymbol) {
			SymbolTable.error(
					methodNode.getContext(),
					body.getContext().start,
					"Type " + bodyType + " of the body of method " + methodName
							+ " is incompatible with declared return type " + retSymbol
			);
		}
		scope = classScope;

		return null;
	}

	@Override
	public TypeSymbol visit(ASTAttributeNode attributeNode) {
		var symbol = attributeNode.getIdSymbol();
		if (symbol == null) {
			return null;
		}

		var typeSymbol= symbol.getType();
		var value = attributeNode.getValue();
		if (value != null) {
			var valueType = value.accept(this);
//			System.out.println("checking that " + typeSymbol.getName() + " inherits " + valueType);

			if (!typeSymbol.inherits(valueType)) {
				SymbolTable.error(
						attributeNode.getContext(),
						attributeNode.getType(),
						"TODO"
				);
			}
		}

		return typeSymbol;
	}

	@Override
	public TypeSymbol visit(ASTLocalVarNode localVarNode) {
		if (localVarNode.getIdSymbol() == null) {
			return null;
		}

		var varName = localVarNode.getName().getText();
		var varType = localVarNode.getType().getText();

		var varTypeSymbol = SymbolTable.globals.lookup(varType);
		if (varTypeSymbol == null) {
			SymbolTable.error(
					localVarNode.getContext(),
					localVarNode.getType(),
					"Let variable " + varName + " has undefined type " + varType
			);
			return null;
		}

		var value = localVarNode.getValue();
		if (value != null) {
			var currentScope = scope;
			scope = scope.getParent();
			var valueType = value.accept(this);
			scope = currentScope;

			// TODO: scoate != null cand implementezi expresii
			if (valueType != null && !valueType.inherits((TypeSymbol)varTypeSymbol)) {
				SymbolTable.error(
						localVarNode.getContext(),
						localVarNode.getType(),
						"TODO"
				);
			}
		}

		localVarNode.getIdSymbol().setType((TypeSymbol)varTypeSymbol);

		return null;
	}

	@Override
	public TypeSymbol visit(ASTIntNode intNode) {
		return TypeSymbol.INT;
	}

	@Override
	public TypeSymbol visit(ASTIdNode idNode) {
		var idName = idNode.getSymbol();

		var idSymbol = scope.lookup(idName);
		if (idSymbol == null) {
			SymbolTable.error(
					idNode.getContext(),
					idNode.getContext().getStop(),
					"Undefined identifier " + idName
			);
			return null;
		}

		return ((IdSymbol)idSymbol).getType();
	}

	@Override
	public TypeSymbol visit(ASTBoolNode boolNode) {
		return TypeSymbol.BOOL;
	}

	@Override
	public TypeSymbol visit(ASTStringNode stringNode) {
		return TypeSymbol.STRING;
	}

	@Override
	public TypeSymbol visit(ASTAssignNode assignNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTNewNode newNode) {
		var typeName = newNode.getType().getText();

		var type = SymbolTable.globals.lookup(typeName);
		if (type == null) {
			SymbolTable.error(
					newNode.getContext(),
					newNode.getType(),
					"Undefined type " + typeName
			);
			return null;
		}

		return (TypeSymbol)type;
	}

	@Override
	public TypeSymbol visit(ASTDispatchNode dispatchNode) {
		return null;
	}

	@Override
	public TypeSymbol visit(ASTBinaryOperatorNode binaryOpNode) {
		var leftSymb = binaryOpNode.getLeftOp().accept(this);
		var rightSymb = binaryOpNode.getRightOp().accept(this);

		if (leftSymb == null || rightSymb == null) {
			if (binaryOpNode instanceof ASTRelOpNode) {
				return TypeSymbol.BOOL;
			}
			return TypeSymbol.INT;
		}

		var leftName = leftSymb.getName();
		var rightName = rightSymb.getName();

		if (binaryOpNode.getSymbol().equals("=")) {
			if (!leftSymb.isEqCompatible(rightSymb)) {
				SymbolTable.error(
						binaryOpNode.getContext(),
						((CoolParser.RelOpContext)binaryOpNode.getContext()).op,
						"Cannot compare " + leftName + " with " + rightName
				);
			}
		} else {
			if (leftSymb != TypeSymbol.INT && rightSymb == TypeSymbol.INT) {
				SymbolTable.error(
						binaryOpNode.getContext(),
						binaryOpNode.getContext().start,
						"Operand of " + binaryOpNode.getSymbol() + " has type " + leftSymb
								+ " instead of Int"
				);
			}
			if (leftSymb == TypeSymbol.INT && rightSymb != TypeSymbol.INT) {
				SymbolTable.error(
						binaryOpNode.getContext(),
						binaryOpNode.getContext().stop,
						"Operand of " + binaryOpNode.getSymbol() + " has type " + rightSymb
								+ " instead of Int"
				);
			}
		}

		return binaryOpNode instanceof ASTRelOpNode ? TypeSymbol.BOOL : TypeSymbol.INT;
	}

	@Override
	public TypeSymbol visit(ASTUnaryOperatorNode unaryOpNode) {
		TypeSymbol opType = unaryOpNode.getOp().accept(this);
		if (opType == null) {
			return null;
		}

		if (unaryOpNode instanceof ASTNegNode && opType != TypeSymbol.INT) {
			SymbolTable.error(
					unaryOpNode.getContext(),
					unaryOpNode.getContext().stop,
					"Operand of " + unaryOpNode.getSymbol() + " has type " + opType.getName()
							+ " instead of Int"
			);
			return TypeSymbol.INT;
		}
		if (unaryOpNode instanceof ASTNotNode && opType != TypeSymbol.BOOL) {
			SymbolTable.error(
					unaryOpNode.getContext(),
					unaryOpNode.getContext().stop,
					"Operand of " + unaryOpNode.getSymbol() + " has type " + opType.getName()
							+ " instead of Bool"
			);
			return TypeSymbol.BOOL;
		}
		if (unaryOpNode instanceof ASTIsVoidNode) {
			return TypeSymbol.BOOL;
		}

		return opType;
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
		scope = letNode.getLetSymbol();
		letNode.getLocals().forEach(local -> local.accept(this));
		letNode.getBody().accept(this);
		scope = scope.getParent();

		return null;
	}

	@Override
	public TypeSymbol visit(ASTCaseBranchNode caseBranchNode) {
		var typeName = caseBranchNode.getType().getText();
		if (typeName.equals("SELF_TYPE")) {
			return null;
		}

		// TODO: fa un scope cu  tipu' asta inainte sa te duci pe expresie
		var caseType = SymbolTable.globals.lookup(typeName);
		if (caseType == null) {
			SymbolTable.error(
					caseBranchNode.getContext(),
					caseBranchNode.getType(),
					"Case variable " + caseBranchNode.getId().getText() + " has undefined type " + typeName
			);
			return null;
		}

		return caseBranchNode.getBody().accept(this);
	}

	@Override
	public TypeSymbol visit(ASTCaseNode caseNode) {
		return caseNode
				.getBranches()
				.stream()
				.map(br -> br.accept(this))
				.filter(Objects::nonNull)
				.reduce((first, second) -> second).orElse(null);
	}

	@Override
	public TypeSymbol visit(ASTBlockNode blockNode) {
//		return blockNode
//				.getExpressions()
//				.stream()
//				.map(expr -> expr.accept(this))
//				.filter(Objects::nonNull)
//				.reduce((first, second) -> second).orElse(null);
		return null;
	}
}
