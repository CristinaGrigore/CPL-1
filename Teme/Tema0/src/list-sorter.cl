class ListSorter {
	const: Constants <- new Constants;
	atoi: A2I <- new A2I;
	caster: Caster <- new Caster;

	sort(lists: List, index: String, type: String, mode: String): Object {
		let cmp: Comparator in {
			if type = new PriceComparator.type_name() then
				cmp <- new PriceComparator.init(mode)
			else if type = new RankComparator.type_name() then
				cmp <- new RankComparator.init(mode)
			else if type = new AlphabeticComparator.type_name() then
				cmp <- new AlphabeticComparator.init(mode)
			else
				self
			fi fi fi;

			sortInner(lists, atoi.a2i(index), cmp);
		}
	};

	sortInner(lists: List, index: Int, cmp: Comparator): Object {
		if index = 1 then
			lists.init(caster.objectToList(lists.head()).sortBy(cmp),
				lists.tail())
		else
			sortInner(lists.tail(), index - 1, cmp)
		fi
	};
};
