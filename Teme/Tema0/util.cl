(* Think of these as abstract classes *)
class Comparator {
    compareTo(o1: Object, o2: Object): Int { 0 };
};

class Filter {
    filter(o: Object): Bool { true };
};

(* TODO: implement specified comparators and filters*)