(**
 * Cele doua stari in care se poate gasi programul sunt "load", in care acesta
 * citeste elemente intr-o noua lista, si "action", in care programul primeste
 * comenzi pe care le executa pe listele stocate pana in momentul fiecarei
 * comenzi.
 *)
class State {
	state: String;

	init(s: String): Object {{
		state <- s;

		if s = "action" then
			self
		else if s = "load" then
			self
		else
			abort()
		fi fi;
	}};

	getState(): String { state };
};
