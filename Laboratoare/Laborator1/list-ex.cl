(*
	Laborator COOL.
*)

(*
	Exercițiul 1.

    Implementați funcția fibonacci, utilizând atât varianta recursivă,
    cât și cea iterativă.

    fibo(0) = 0
    fibo(1) = 1
    fibo(2) = 1
    fibo(3) = 2
    fibo(4) = 3
    fibo(5) = 5
    fibo(6) = 8
*)
class Fibo {
	fibo_rec(n : Int) : Int {
		if n <= 1 then
			n
		else
			fibo_rec(n - 1) + fibo_rec(n - 2)
		fi
	};

	fibo_iter(n : Int) : Int {
		let f2: Int <- 0,
			f1: Int <- 1,
			f: Int
		in {
			if n = 0 then
				0
			else {
				while (1 < n) loop {
					f <- f1 + f2;
					f2 <- f1;
					f1 <- f;

					n <- n - 1;
				} pool;

				f1;
			} fi;
		}
	};
};

class FizzBuzz inherits IO {
	fizzBuzz(n: Int): IO {
		if modulo(n, 3) = 0 then
			if modulo(n, 5) = 0 then
				out_string("FizzBuzz")
			else
				out_string("Fizz")
			fi
		else if modulo(n, 5) = 0 then
			out_string("Buzz")
		else
			out_int(n)
		fi fi
	};

	modulo(n: Int, d: Int): Int { n - (d * (n / d)) };
};

(*
	Exercițiul 2.

	Pornind de la ierarhia de clase implementată la curs, aferentă listelor
	(găsiți clasele List și Cons mai jos), implementați următoarele funcții
	și testați-le. Este necesară definirea lor în clasa List și supradefinirea
	în clasa Cons.

	* append: întoarce o nouă listă rezultată prin concatenarea listei curente
		(self) cu lista dată ca parametru;
	* reverse: întoarce o nouă listă cu elementele în ordine inversă.
*)

(*
	Listă omogenă cu elemente de tip Int. Clasa List constituie rădăcina
	ierarhiei de clase reprezentând liste, codificând în același timp
	o listă vidă.

	Adaptare după arhiva oficială de exemple a limbajului COOL.
*)
class List inherits IO {
	isEmpty(): Bool { true };

	-- 0, deși cod mort, este necesar pentru verificarea tipurilor
	hd(): Int {{
		abort();
		0;
	}};

	-- Similar pentru self
	tl(): List {{
		abort();
		self;
	}};

	cons(h: Int): Cons { new Cons.init(h, self) };

	append(l : List): List { l };

	reverse(l: List): List { l };

	print(): IO { out_string("\n") };

	map(m: Map): List { self };

	filter(f: Filter): List { self };
};

(*
	În privința vizibilității, atributele sunt implicit protejate, iar metodele,
	publice.

	Atributele și metodele utilizează spații de nume diferite, motiv pentru care
	hd și tl reprezintă nume atât de atribute, cât și de metode.
*)
class Cons inherits List {
	hd : Int;
	tl : List;

	init(h: Int, t: List): Cons {{
		hd <- h;
		tl <- t;
		self;
	}};

	-- Supradefinirea funcțiilor din clasa List
	isEmpty(): Bool { false };

	hd(): Int { hd };

	tl(): List { tl };

	print(): IO {{
		out_int(hd);
		out_string(" ");
		-- Mecanismul de dynamic dispatch asigură alegerea implementării
		-- corecte a metodei print.
		tl.print();
	}};

	reverse(l: List): List { tl.reverse(l.cons(hd)) };

	append(l: List): List { new Cons.init(hd, tl.append(l)) };

	map(m: Map): List { new Cons.init(m.apply(hd), tl.map(m)) };

	filter(f: Filter): List {
		if f.apply(hd) then
			new Cons.init(hd, tl.filter(f))
		else
			tl.filter(f)
		fi
	};
};

(*
	Exercițiul 3.

	Scopul este implementarea unor mecanisme similare funcționalelor
	map și filter din limbajele funcționale. map aplică o funcție pe fiecare
	element, iar filter reține doar elementele care satisfac o anumită condiție.
	Ambele întorc o nouă listă.

	Definiți clasele schelet Map, respectiv Filter, care vor include unica
	metodă apply, având tipul potrivit în fiecare clasă, și implementare
	de formă.

	Pentru a defini o funcție utilă, care adună 1 la fiecare element al listei,
	definiți o subclasă a lui Map, cu implementarea corectă a metodei apply.

	În final, definiți în cadrul ierarhiei List-Cons o metodă map, care primește
	un parametru de tipul Map.

	Definiți o subclasă a subclasei de mai sus, care, pe lângă funcționalitatea
	existentă, de incrementare cu 1 a fiecărui element, contorizează intern
	și numărul de elemente prelucrate. Utilizați static dispatch pentru apelarea
	metodei de incrementare, deja definită.

	Repetați pentru clasa Filter, cu o implementare la alegere a metodei apply.
*)
class Map inherits Cons {
	apply(n: Int): Int { n };
};

class Filter inherits Cons {
	apply(n: Int): Bool { true };
};

class MapInc inherits Map {
	apply(n: Int): Int { n + 1 };
};

class FilterIsEven inherits Filter {
	apply(n: Int): Bool { n / 2 * 2 = n };
};

-- Testați în main.
class Main inherits IO {
	main() : Object {{
		let list1 : List <- new List.cons(1).cons(2).cons(3),
            list2 : List <- new List.cons(6).cons(7)
        in {
			list1.print();
			list2.print();

			out_string("Append:\n");
			list1.append(list2).print();

			out_string("Reverse:\n");
			list1.reverse(new List).print();

			out_string("Original Strings:\n");
			list1.print();
			list2.print();

			out_string("map(+ 1, list1.append(list2)) = ");
			list1.append(list2).map(new MapInc).print();

			out_string("filter(isEven, list1.append(list2)) = ");
			list1.append(list2).filter(new FilterIsEven).print();
		};

		-- expecting fibo(6) = 8
		let fib: Fibo <- new Fibo
		in {
			out_string("Implementarea recursiva - fib(6) = ");
			out_int(fib.fibo_rec(6));
			out_string("\n\n");
			out_string("Implementarea iterativa - fib(6) = ");
			out_int(fib.fibo_iter(6));
			out_string("\n");
		};

		let fizz: FizzBuzz <- new FizzBuzz
		in {
			out_string("\n");
			fizz.fizzBuzz(6);
			out_string("\n");
			fizz.fizzBuzz(30);
			out_string("\n");
			fizz.fizzBuzz(55);
			out_string("\n");
			fizz.fizzBuzz(13);
			out_string("\n");
		};
	}};
};
