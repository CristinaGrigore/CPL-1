package cool.visitor;

import cool.AST.*;
import cool.compiler.Compiler;
import cool.parser.CoolParser;
import cool.scopes.Scope;
import cool.scopes.SymbolTable;
import cool.symbols.IdSymbol;
import cool.symbols.TypeSymbol;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public class CodeGenVisitor implements ASTVisitor<ST> {
	private final STGroupFile templates;
	private long cnt;
	Scope scope;

	private final ST constants;
	private final ST classNames;
	private final ST classObjs;
	private final ST protObjs;
	private final ST dispTables;
	private final ST methods;

	private final HashMap<String, ST> strings;
	private final HashMap<Integer, ST> ints;

	private String addString(String s) {
		String updated = s.replace("-", "_");

		if (!strings.containsKey(updated)) {
			int len = updated.length();
			ST st = templates.getInstanceOf("stringConst")
					.add("label", updated)
					.add("str", s)
					.add("len", addInt(len))
					.add("wordCount", (len + 1) / 4 + 5);

			strings.put(updated, st);
		}

		return "str_const_" + updated;
	}

	private String addInt(int n) {
		if (!ints.containsKey(n)) {
			ints.put(n, templates.getInstanceOf("intConst").add("val", n));
		}

		return "int_const_" + n;
	}

	private ST makeInitMethod(ASTClassNode node) {
		var type = node.getType();
		var attribs = node
				.getContent()
				.stream()
				.filter(content -> content instanceof ASTAttributeNode)
				.map(attrib -> attrib.accept(this))
				.filter(Objects::nonNull)
				.map(ST::render)
				.collect(Collectors.joining("\n"));

		return templates.getInstanceOf("initMethod")
				.add("class", type)
				.add("parent", type.getParentName())
				.add("attrib", attribs);
	}

	public CodeGenVisitor() {
		templates = new STGroupFile("cgen.stg");
		strings = new HashMap<>();
		ints = new HashMap<>();
		cnt = 0;

		addInt(0);
		addString("");

		constants = templates.getInstanceOf("sequence");
		classNames = templates.getInstanceOf("sequence");
		classObjs = templates.getInstanceOf("sequence");
		dispTables = templates.getInstanceOf("sequence");
		protObjs = templates.getInstanceOf("sequence");
		methods = templates.getInstanceOf("sequenceSpaced");

		SymbolTable.globals.getSymbols()
				.values()
				.stream()
				.sorted(Comparator.comparingInt(ts -> ((TypeSymbol)ts).getTag()))
				.forEach(clss -> {
					if (clss != TypeSymbol.SELF_TYPE) {
						var className = clss.getName();
						dispTables.add("e", ((TypeSymbol)clss).getDispTable(templates));
						classObjs.add("e", templates.getInstanceOf("objTabEntry")
								.add("class", clss));
						classNames.add("e", "\t.word\t" + addString(className));
						protObjs.add("e", ((TypeSymbol)clss).getProtObj(templates));
						addInt(className.length());
					}
				});

		methods.add("e", TypeSymbol.OBJECT.getInitMethod(templates));
		methods.add("e", TypeSymbol.INT.getInitMethod(templates));
		methods.add("e", TypeSymbol.BOOL.getInitMethod(templates));
		methods.add("e", TypeSymbol.STRING.getInitMethod(templates));
		methods.add("e", TypeSymbol.IO.getInitMethod(templates));
	}

	@Override
	public ST visit(ASTProgramNode programNode) {
		programNode.getClasses().forEach(this::visit);

		ints.values().forEach(ct -> constants.add("e", ct));
		strings.values().forEach(ct -> constants.add("e", ct));

		ST program = templates.getInstanceOf("program");
		program.add("consts", constants);
		program.add("classNames", classNames);
		program.add("classObjs", classObjs);
		program.add("protObjs", protObjs);
		program.add("dispTables", dispTables);
		program.add("methods", methods);

		return program;
	}

	@Override
	public ST visit(ASTClassNode classNode) {
		scope = classNode.getType();

		methods.add("e", makeInitMethod(classNode));

		classNode.getContent().forEach(content -> {
			if (content instanceof ASTMethodNode) {
				ST methodST = content.accept(this).add("class", classNode.getType());
				methods.add("e", methodST);
			}
		});
		return null;
	}

	@Override
	public ST visit(ASTFormalNode formalNode) {
		return null;
	}

	@Override
	public ST visit(ASTMethodNode methodNode) {
		scope = methodNode.getMethodSymbol();
		var methodST = templates.getInstanceOf("method")
				.add("name", methodNode.getMethodSymbol())
				.add("body", methodNode.getBody().accept(this))
				.add("numPops", methodNode.getParams().size() * 4 + 12);
		scope = scope.getParent();

		return methodST;
	}

	@Override
	public ST visit(ASTAttributeNode attributeNode) {
		var expr = attributeNode.getValue();
		if (expr == null) {
			return null;
		}

		return templates.getInstanceOf("storeAttrib")
				.add("val", expr.accept(this))
				.add("offset", attributeNode.getIdSymbol().getOffset());
	}

	@Override
	public ST visit(ASTLocalVarNode localVarNode) {
		var valueExpr= localVarNode.getValue();
		var varSymbol = localVarNode.getIdSymbol();
		var varType = varSymbol.getType();

		String value;
		if (valueExpr == null) {
			if (varType == TypeSymbol.INT) {
				value = "\tla\t\t$a0 int_const_0";
			} else if (varType == TypeSymbol.STRING) {
				value = "\tla\t\t$a0 str_const_";
			} else if (varType == TypeSymbol.BOOL) {
				value = "\tla\t\t$a0 bool_const_false";
			} else {
				value = "\tli\t\t$a0 0";
			}
		} else {
			value = valueExpr.accept(this).render();
		}

		return templates.getInstanceOf("storeVar")
				.add("val", value)
				.add("offset", varSymbol.getOffset());
	}

	@Override
	public ST visit(ASTIntNode intNode) {
		return templates.getInstanceOf("literal")
				.add("addr", addInt(Integer.parseInt(intNode.getSymbol())));
	}

	@Override
	public ST visit(ASTIdNode idNode) {
		var idName = idNode.getSymbol();
		var idSymbol = (IdSymbol)scope.lookup(idName);
		ST idST;

		if (idSymbol.isAttribute()) {
			idST = templates.getInstanceOf("attribute");
		} else {
			idST = templates.getInstanceOf("loadVar");
		}

		return idST.add("offset", idSymbol.getOffset());
	}

	@Override
	public ST visit(ASTBoolNode boolNode) {
		return templates.getInstanceOf("literal")
				.add("addr", "bool_const_" + boolNode.getSymbol());
	}

	@Override
	public ST visit(ASTStringNode stringNode) {
		return templates.getInstanceOf("literal")
				.add("addr", addString(stringNode.getSymbol()));
	}

	@Override
	public ST visit(ASTAssignNode assignNode) {
		ST value = assignNode.getValue().accept(this);
		var idSymbol = (IdSymbol)scope.lookup(assignNode.getId().getText());
		ST assignST;

		if (idSymbol.isAttribute()) {
			assignST = templates.getInstanceOf("storeAttrib");
		} else {
			assignST = templates.getInstanceOf("storeVar");
		}

		return assignST.add("val", value).add("offset", idSymbol.getOffset());
	}

	@Override
	public ST visit(ASTNewNode newNode) {
		return null;
	}

	@Override
	public ST visit(ASTDispatchNode dispatchNode) {
		var method = dispatchNode.getCallee().getText();
		int offset = dispatchNode.getCallerType().lookupMethod(method).getOffset();

		var reversedParams = dispatchNode.getParams();
		Collections.reverse(reversedParams);

		var params = reversedParams.stream().map(paramExpr -> {
			var param = paramExpr.accept(this);
			return templates.getInstanceOf("dispatchParam")
					.add("param", param).render();
		}).collect(Collectors.joining("\n"));

		var ctx = dispatchNode.getContext();
		while (!(ctx.getParent() instanceof CoolParser.ProgramContext)) {
			ctx = ctx.getParent();
		}
		var fileName = new File(Compiler.fileNames.get(ctx)).getName();

		ST dispatch = templates.getInstanceOf("dispatch")
				.add("method", method)
				.add("idx", cnt++)
				.add("params", params)
				.add("offset", offset)
				.add("filename", addString(fileName))
				.add("line", dispatchNode.getCallee().getLine());

		var exprCaller = dispatchNode.getCaller();
		var caller = exprCaller != null && !exprCaller.getSymbol().equals("self")
				? exprCaller.accept(this)
				: null;
		if (caller != null) {
			dispatch.add("explicit", caller);
		}

		return dispatch;
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
		ST initST = templates.getInstanceOf("initLet")
				.add("space", letNode.getLocals().size() * -4);
		ST letST = templates.getInstanceOf("sequence").add("e", initST);
		int i = -4;

		scope = letNode.getLetSymbol();
		for (var local : letNode.getLocals()) {
			local.getIdSymbol().setOffset(i);
			letST.add("e", visit(local));
			i -= 4;
		}

		letST.add("e", letNode.getBody().accept(this));
		scope = scope.getParent();

		ST finST = templates.getInstanceOf("initLet")
				.add("space", letNode.getLocals().size() * 4);
		letST.add("e", finST);

		return letST;
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
		ST blockST = templates.getInstanceOf("sequence");
		blockNode.getExpressions().forEach(expr -> blockST.add("e", expr.accept(this)));

		return blockST;
	}
}
