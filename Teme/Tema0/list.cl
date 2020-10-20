class List {
    head: Object;
	tail: List;

    isEmpty(): Bool { isvoid head };

    head(): Object { head };

    tail(): List { tail };

    init(o: Object, l: List): List {{
		head <- o;
        tail <- l;
		self;
	}};

    add(o: Object): List {
        if isEmpty() then
            init(o, new List)
        else
            tail.add(o)
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
