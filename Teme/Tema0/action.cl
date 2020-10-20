class Action inherits IO {
	stringify: Stringify <- new Stringify;
	const: Constants <- new Constants;
	-- io: IO <- new IO;

	execute(action: String, lists: List): Object {
		if action =  then
			print(1, lists)
		else if action.substr(0, 5) = const.actionPrint() then
			printNth(1, new A2I.a2i(action.substr(6, action.length() - 6)),
				lists)
		else
			out_string("TODO\n")
		fi fi
	};

	print(pos: Int, lists: List): Object {
		if not lists.isEmpty() then {
			out_int(pos).out_string(": ".concat(formatList(lists.head())));
			print(pos + 1, lists.tail());
		} else {
			self;
			out_string("empty\n");
		} fi
	};

	printNth(crtPos: Int, targetPos: Int, lists: List): Object {
		if crtPos < targetPos then
			printNth(crtPos + 1, targetPos, lists.tail())
		else
			out_string(formatList(lists.head()))
		fi
	};

	-- TODO: schimba in List daca se poate
	formatList(list: Object): String {
		"[ ".concat(stringify.toString(list)).concat(" ]\n")
	};
};
