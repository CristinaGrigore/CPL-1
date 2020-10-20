class ListPrinter inherits IO {
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
			out_int(pos).out_string(": "
				.concat(objectToList(lists.head()).toString()));
			print(pos + 1, lists.tail());
		} else
			self
		fi
	};

	printNth(crtPos: Int, targetPos: Int, lists: List): IO {
		if crtPos < targetPos then
			printNth(crtPos + 1, targetPos, lists.tail())
		else
			out_string(objectToList(lists.head()).toString())
		fi
	};

	objectToList(obj: Object): List {
		case obj of
		list: List => list;
		esac
	};
};
