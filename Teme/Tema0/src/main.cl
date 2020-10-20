class Main inherits IO {
	lists : List <- new List;
	state: State <- new State;
	parser: StringParser <- new StringParser;
	const: Constants <- new Constants;
	tokenizer: StringTokenizer <- new StringTokenizer;
	printer: ListPrinter <- new ListPrinter;
	filterer: ListFilterer <- new ListFilterer;

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
					else if token = const.actionFilter() then
						let pos: String <- tokenizer.nextToken(),
							filterType: String <- tokenizer.nextToken()
						in
							filterer.applyFilter(lists, filterType, pos)
					else
						self
					fi fi fi
				else if state.getState() = const.stateLoad() then
					if inputStr = const.endLoad() then {
						state.init(const.stateAction());
						lists.add(crtList);
						crtList <- new List;
					} else
						case parser.parseString(inputStr) of
							invalid: Invalid => self;
							obj: Object => crtList.add(obj);
						esac
					fi
				else
					out_string("TODO\n")
				fi fi;
			} pool;
	}};
};
