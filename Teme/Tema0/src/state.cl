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
