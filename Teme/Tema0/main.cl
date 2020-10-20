class Main inherits IO {
    lists : List <- new List;
    state: State <- new State;
    parser: StringParser <- new StringParser;
    const: Constants <- new Constants;
    inputStr: String;

    main(): Object {{
        state.init(const.stateLoad());

        let crtList: List <- new List,
            action: Action <- new Action
        in {
            while true loop {
                inputStr <- in_string();

                if state.getState() = const.stateAction() then
                    action.execute(inputStr, lists)
                else if state.getState() = const.stateLoad() then {
                    if inputStr = const.endLoad() then {
                        state.init(const.stateAction());
                        lists.add(crtList);
                    } else
                        crtList.add(parser.parseString(inputStr))
                    fi;
                } else
                    out_string("TODO\n")
                fi fi;
            } pool;
        };
    }};
};
