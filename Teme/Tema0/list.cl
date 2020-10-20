class List inherits IO {
	head: Object;
	tail: List;

	isEmpty(): Bool { isvoid head };

	head(): Object { head };

	tail(): List { tail };

	init(o: Object, l: List): List {{
		head <- o;
		tail <- l;
		self;
	}};

	add(o: Object): List {
		if isEmpty() then
			init(o, new List)
		else
			tail.add(o)
		fi
	};

	stringify(): String {
		case head of
		iHead: Int =>
			iHead.type_name().concat("(").concat(new A2I.i2a(iHead))
				.concat(")");
		sHead: String =>
			sHead.type_name().concat("(").concat(sHead).concat(")");
		bHead: Bool =>
			if bHead then
				bHead.type_name().concat("(true)")
			else
				bHead.type_name().concat("(false)")
			fi;
		pHead: Product => pHead.toString();
		rHead: Rank => rHead.toString();
		ioHead: IO => "IO()";
		oHead: Object => "Object()";
		esac
	};

	toStringInner(): String {
		if tail.isEmpty() then
			stringify().concat(" ]\n")
		else
			stringify().concat(", ").concat(tail.toStringInner())
		fi
	};

	toString(): String { "[ ".concat(toStringInner()) };

	merge(other: List): SELF_TYPE {
		self (* TODO *)
	};

	filterBy(): SELF_TYPE {
		self (* TODO *)
	};

	sortBy(): SELF_TYPE {
		self (* TODO *)
	};
};
