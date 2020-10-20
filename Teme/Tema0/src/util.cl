class Comparator {
    compareTo(o1: Object, o2: Object): Int { 0 };
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
