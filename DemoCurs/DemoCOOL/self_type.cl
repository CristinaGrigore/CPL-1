class Main inherits IO {
	main(): Object {{
		-- fara SELF_TYPE nu se putea executa out_string(...).f() pt ca f e
		-- definit in Main
		let m: Main <- new Main
		in m.out_string("ceva\n").f();
	}};

	f(): IO {
		out_string("altceva\n")
	};
};