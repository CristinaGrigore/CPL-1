class List inherits IO {
	isEmpty(): Bool {
		true
	};

	hd(): Int {
		-- functia intoarce Int si abort() intoarce Object => trebuie pacalit
		-- verificatorul de tip
		{
			abort();
			0;  -- nu se ajunge aici
		}
	};

	tl(): List {
		{
			abort();
			self;
		}
	};

	-- nu modificam lista curenta, ci cream o alta lista: [e, self]
	cons(e: Int): List {
		new Cons.init(e, self)
	};

	print(): IO {
		out_string("\n")
	};
};

class Cons inherits List {
	hd: Int;
	tl: List;

	init(h: Int, t: List): List {{
		hd <- h;
		tl <- t;
		self;
	}};

	-- supraincarcare
	isEmpty(): Bool { false };

	hd(): Int { hd };

	tl(): List { tl };

	print(): IO {{
		out_int(hd);
		tl.print();
	}};
};

class Main inherits IO {
	main(): Object {{
		let list: List <- new List.cons(1).cons(2).cons(3),
			temp: List <- list
		in {
			list.print();

			while not temp.isEmpty() loop {
				out_int(temp.hd());
				temp <- temp.tl();
			} pool;
		};
	}};
};
