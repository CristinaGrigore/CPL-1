class ListFilterer {
	caster: Caster <- new Caster;
	atoi: A2I <- new A2I;

	applyFilter(lists: List, index: String, type: String): Object {
		let f: Filter in {
			if type = new ProductFilter.type_name() then
				f <- new ProductFilter
			else if type = new RankFilter.type_name() then
				f <- new RankFilter
			else if type = new SamePriceFilter.type_name() then
				f <- new SamePriceFilter
			else
				self
			fi fi fi;

			applyFilterInner(lists, atoi.a2i(index), f);
		}
	};

	applyFilterInner(lists: List, index: Int, f: Filter): Object {
		if index = 1 then
			lists.init(caster.objectToList(lists.head()).filter(f),
				lists.tail())
		else
			applyFilterInner(lists.tail(), index - 1, f)
		fi
	};
};