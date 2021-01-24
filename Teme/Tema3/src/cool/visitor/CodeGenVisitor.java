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
import java.util.HashMap;
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

		SymbolTable.globals.getSymbols().values().forEach(clss -> {
			if (clss != TypeSymbol.SELF_TYPE) {
				var className = clss.getName();
				dispTables.add("e", ((TypeSymbol)clss).getDispTable(templates));
				classObjs.add("e", templates.getInstanceOf("objTabEntry").add("class", clss));
				classNames.add("e", "\t.word\t" + addString(className));
				methods.add("e", ((TypeSymbol)clss).getInitMethod(templates));
				protObjs.add("e", ((TypeSymbol)clss).getProtObj(templates));
				addInt(className.length());
			}
		});
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
		return templates.getInstanceOf("method")
				.add("name", methodNode.getMethodSymbol())
				.add("body", methodNode.getBody().accept(this));
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
		return templates.getInstanceOf("literal")
				.add("addr", addInt(Integer.parseInt(intNode.getSymbol())));
	}

	@Override
	public ST visit(ASTIdNode idNode) {
		var idName = idNode.getSymbol();
		var idSymbol = (IdSymbol)scope.lookup(idName);

		if (idSymbol.isAttribute()) {
			return  templates.getInstanceOf("attribute").add("offset", idSymbol.getOffset());
		}

		// TODO: adauga formal + let
		return templates.getInstanceOf("attribute");
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
		return null;
	}

	@Override
	public ST visit(ASTNewNode newNode) {
		return null;
	}

	@Override
	public ST visit(ASTDispatchNode dispatchNode) {
		var method = dispatchNode.getCallee().getText();
		// TODO: offsetu' e gresit cand e dispatch explicit
		int offset = ((TypeSymbol)scope.getParent()).lookupMethod(method).getOffset();

		var params = dispatchNode.getParams().stream().map(paramExpr -> {
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
			// TODO: dispatch pe formal, let sau cu @
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
		ST blockST = templates.getInstanceOf("sequence");
		blockNode.getExpressions().forEach(expr -> blockST.add("e", expr.accept(this)));

		return blockST;
	}
}
