class Main inherits IO {
	main() : Object {{
		let io: IO <- new IO
		in {
			io.out_string("Hello, CPL!\n").out_string("Bye!\n");
			io.out_string(io.type_name());
		};
		out_int(fact_iter(in_int()));
		out_string(type_name());
	}};

	fact_rec(n: Int): Int {
		if n = 0 then
			1
		else
			n * fact_rec(n - 1)
		fi
	};

	fact_iter(n: Int): Int {
		let p: Int <- 1
		in {
			while (0 < n) loop {
				p <- p * n;

				-- se calc un nou obiect (n - 1) si se pierde ref la vechiul n
				-- dealocarea vechiului n depinde de garbage collector
				n <- n - 1;
			} pool;  -- intoarece void: Object

			p;
		}
	};
};
