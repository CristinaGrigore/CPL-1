class ListFilterer {
	caster: Caster <- new Caster;
	atoi: A2I <- new A2I;

	applyFilter(lists: List, filter: String, pos: String): Object {
		let f: Filter in {
			if filter = new ProductFilter.type_name() then
				f <- new ProductFilter
			else if filter = new RankFilter.type_name() then
				f <- new RankFilter
			else if filter = new SamePriceFilter.type_name() then
				f <- new SamePriceFilter
			else
				self
			fi fi fi;

			applyFilterInner(lists, f, atoi.a2i(pos));
		}
	};

	applyFilterInner(lists: List, f: Filter, pos: Int): Object {
		if pos = 1 then
			lists.init(caster.objectToList(lists.head()).filter(f),
				lists.tail())
		else
			applyFilterInner(lists.tail(), f, pos - 1)
		fi
	};
};