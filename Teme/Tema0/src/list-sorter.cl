(**
 * Sorteaza o lista data prin indexul sau, crescator sau descrescator, folosind
 * unul dintre comparatorii {PriceComparator, RankComparator,
 * AlphabeticComparator}.
 *)
class ListSorter inherits A2I {
	const: Constants <- new Constants;
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

			sortInner(lists, a2i(index), cmp);
		}
	};

	sortInner(lists: List, index: Int, cmp: Comparator): Object {
		if index = 1 then
			-- Se reinitializeaza lista curenta cu ea insasi sortata si tailul
			-- sau.
			lists.init(caster.objectToList(lists.head()).sortBy(cmp),
				lists.tail())
		else
			sortInner(lists.tail(), index - 1, cmp)
		fi
	};
};
