class List {
    hd: Object;
	tl: List;

    isEmpty(): Bool { isvoid(hd) };

    init(h: Object, l: List): List {{
		hd <- h;
        tl <- l;
		self;
	}};

	head(): Object { hd };

	tail(): List { tl };

    add(o: Object): List {
        if isEmpty() then
            init(o, new List)
        else
            tl.add(o)
        fi
    };

    toString(): String {
        "[TODO: implement me]"
    };

    merge(other: List): SELF_TYPE {
        self (* TODO *)
    };

    filterBy(): SELF_TYPE {
        self (* TODO *)
    };

    sortBy(): SELF_TYPE {
        self (* TODO *)
    };
};
