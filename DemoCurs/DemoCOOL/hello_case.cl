class Main inherits IO {
	main(): Object {
		let x: Object <- "abc"
		in case x of  -- se alege cel mai particular tip dinamic cu cel al lui x
			s: String => out_string(s);  -- s e acelasi obiect ca x, da' cu tipu' String
			n: Int => out_int(n);
		esac  -- daca nu gaseste niciun match => eroare la runtime
	};
};