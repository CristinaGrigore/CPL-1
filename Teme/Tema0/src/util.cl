class Comparator {
    -- Intoarce Bool pentru a fi compatibil cu compararea stringurilor.
    compareTo(o1: Object, o2: Object): Bool { false };
};

class PriceComparator inherits Comparator {
    const: Constants <- new Constants;
    dir: String;

    init(direction: String): SELF_TYPE {{
        dir <- direction;
        self;
    }};

    compareTo(o1: Object, o2: Object): Bool {
        let price1: Int <- getPrice(o1),
            price2: Int <- getPrice(o2)
        in
            if dir = const.directionDescendent() then
                price2 < price1
            else
                price1 < price2
            fi
    };

    getPrice(obj: Object): Int {
        case obj of
        prod: Product => prod.getPrice();
        o: Object => {
            abort();
            0;
        };
        esac
    };
};

class RankComparator inherits Comparator {
    const: Constants <- new Constants;
    dir: String;

    init(direction: String): SELF_TYPE {{
        dir <- direction;
        self;
    }};

    compareTo(o1: Object, o2: Object): Bool {
        let rank1: Int <- getRank(o1),
            rank2: Int <- getRank(o2)
        in
            if dir = const.directionDescendent() then
                rank2 < rank1
            else
                rank1 < rank2
            fi
    };

    getRank(obj: Object): Int {
        case obj of
        r: Private => 0;
        r: Corporal => 1;
        r: Sergent =>  2;
        r: Officer =>  3;
        o: Object => {
            abort();
            0;
        };
        esac
    };
};

class AlphabeticComparator inherits Comparator {
    const: Constants <- new Constants;
    dir: String;

    init(direction: String): SELF_TYPE {{
        dir <- direction;
        self;
    }};

    compareTo(o1: Object, o2: Object): Bool {
        let s1: String <- getString(o1),
            s2: String <- getString(o2)
        in
            if dir = const.directionDescendent() then
                s2 < s1
            else
                s1 < s2
            fi
    };

    getString(obj: Object): String {
        case obj of
        s: String => s;
        o: Object => {
            abort();
            "";
        };
        esac
    };
};


class Filter {
    apply(o: Object): Bool { true };
};

class ProductFilter inherits Filter {
    apply(o: Object): Bool {
        case o of
        prod: Product => true;
        obj: Object => false;
        esac
    };
};

class RankFilter inherits Filter {
    apply(o: Object): Bool {
        case o of
        rank: Rank => true;
        obj: Object => false;
        esac
    };
};

class SamePriceFilter inherits Filter {
    apply(o: Object): Bool {
        case o of
        prod: Product => prod.getPrice() = prod@Product.getPrice();
        obj: Object => false;
        esac
    };
};
