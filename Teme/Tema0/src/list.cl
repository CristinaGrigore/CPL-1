class List inherits IO {
	head: Object;
	tail: List;

	isEmpty(): Bool { isvoid head };

	head(): Object { head };

	tail(): List { tail };

	init(o: Object, l: List): SELF_TYPE {{
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
		if isEmpty() then
			" ]\n"
		else if tail.isEmpty() then
			stringify().concat(" ]\n")
		else
			stringify().concat(", ").concat(tail.toStringInner())
		fi fi
	};

	toString(): String { "[ ".concat(toStringInner()) };

	merge(other: List): SELF_TYPE {
		self (* TODO *)
	};

	filter(f: Filter): List {
		if isEmpty() then
			self
		else if f.apply(head) then
			new List.init(head, tail.filter(f))
		else
			tail.filter(f)
		fi fi
	};

	sortBy(cmp: Comparator): List { sortByInner(new List, cmp) };

	sortByInner(sortedL: List, cmp: Comparator): List {
		if isEmpty() then
			sortedL
		else
			tail.sortByInner(sortedL.insertInSortedList(head, cmp), cmp)
		fi
	};

	insertInSortedList(obj: Object, cmp: Comparator): List {
		if isEmpty() then
			new List.init(obj, self)
		else if cmp.compareTo(head, obj) then
			new List.init(head, tail.insertInSortedList(obj, cmp))
		else
			new List.init(obj, self)
		fi fi
	};
};
