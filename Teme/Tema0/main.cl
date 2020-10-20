class Main inherits IO {
	lists : List <- new List;
	state: State <- new State;
	parser: StringParser <- new StringParser;
	const: Constants <- new Constants;
	tokenizer: StringTokenizer <- new StringTokenizer;
	printer: ListPrinter <- new ListPrinter;

	main(): Object {{
		state.init(const.stateLoad());

		let crtList: List <- new List,
			inputStr: String,
			token: String
		in
			while true loop {
				inputStr <- in_string();
				tokenizer <- tokenizer.init(inputStr);
				token <- tokenizer.nextToken();

				if state.getState() = const.stateAction() then
					if token = const.actionPrint() then {
						token <- tokenizer.nextToken();
						printer.handlePrint(token, lists);
					} else if token = const.stateLoad() then
						state.init(token)
					else
						self
					fi fi
				else if state.getState() = const.stateLoad() then
					if inputStr = const.endLoad() then {
						state.init(const.stateAction());
						lists.add(crtList);
					} else
						-- TODO: verifica daca e Object
						crtList.add(parser.parseString(inputStr))
					fi
				else
					out_string("TODO\n")
				fi fi;
			} pool;
	}};
};
