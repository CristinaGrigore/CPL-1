class List inherits IO {
	elem: Int;
	next: List;

	init(e: Int, n: List): List {{
		elem <- e;
		next <- n;
		self;
	}};

	print(): IO {{
		out_int(elem);
		out_string(" ");

		if not (isvoid next) then
			next.print()
		else
			out_string("\n")
		fi;
	}};
};

class Main inherits IO {
	main(): Object {{
		let empty: List,  -- ca sa tina loc de "void", care nu exista explicit
			list: List <- new List.init(1, new List.init(2, new List.init(3, empty)))
		in
			list.print();
	}};
};
