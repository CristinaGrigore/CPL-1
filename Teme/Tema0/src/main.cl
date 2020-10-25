class Main inherits IO {
	lists : List <- new List;
	state: State <- new State;
	parser: StringParser <- new StringParser;
	const: Constants <- new Constants;
	tokenizer: StringTokenizer <- new StringTokenizer;
	printer: ListPrinter <- new ListPrinter;
	filterer: ListFilterer <- new ListFilterer;
	sorter: ListSorter <- new ListSorter;
	merger: ListMerger <- new ListMerger;
	helper: Helper <- new Helper;

	main(): Object {{
		state.init(const.stateLoad());

		let crtList: List <- new List,
			inputStr: String,
			token: String
		in
			while true loop {
				inputStr <- in_string();
				tokenizer <- tokenizer.init(inputStr, const.space());
				token <- tokenizer.nextToken();

				if state.getState() = const.stateAction() then
					handleAction(token)
				else if state.getState() = const.stateLoad() then
					if inputStr = const.actionHelp() then
						helper.printHelp()
					else if inputStr = const.endLoad() then {
						state.init(const.stateAction());
						lists.add(crtList);
						crtList <- new List;
					} else
						case parser.getListElement(inputStr) of
							invalid: Invalid => self;
							obj: Object => crtList.add(obj);
						esac
					fi fi
				else
					out_string("Unsupported command!\n")
				fi fi;
			} pool;
	}};

	handleAction(command: String): Object {
		if command = const.actionPrint() then
			printer.handlePrint(tokenizer.nextToken(), lists)
		else if command = const.stateLoad() then
			state.init(command)
		else if command = const.actionFilter() then
			filterer.applyFilter(lists, tokenizer.nextToken(),
				tokenizer.nextToken())
		else if command = const.actionSort() then
			sorter.sort(lists, tokenizer.nextToken(), tokenizer.nextToken(),
				tokenizer.nextToken())
		else if command = const.actionMerge() then
			merger.merge(lists, tokenizer.nextToken(), tokenizer.nextToken())
		else if command = const.actionHelp() then
			helper.printHelp()
		else
			self
		fi fi fi fi fi fi
	};
};
