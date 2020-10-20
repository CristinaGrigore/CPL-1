class ListPrinter inherits IO {
	stringify: Stringify <- new Stringify;
	const: Constants <- new Constants;
	atoi: A2I <- new A2I;

	handlePrint(pos: String, lists: List): IO {
		if pos = const.emptyString() then
			print(1, lists)
		else
			printNth(1, atoi.a2i(pos), lists)
		fi
	};

	print(pos: Int, lists: List): IO {
		if not lists.isEmpty() then {
			out_int(pos).out_string(": ".concat(formatList(lists.head())));
			print(pos + 1, lists.tail());
		} else
			self
		fi
	};

	printNth(crtPos: Int, targetPos: Int, lists: List): IO {
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
