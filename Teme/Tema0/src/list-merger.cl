class ListMerger inherits IO {
	const: Constants <- new Constants;
	atoi: A2I <- new A2I;
	caster: Caster <- new Caster;

	merge(lists: List, index1: String, index2: String): List {
		let pos1: Int <- atoi.a2i(index1),
			pos2: Int <- atoi.a2i(index2),
			l1: List <- caster.objectToList(lists.get(pos1)),
			l2: List <- caster.objectToList(lists.get(pos2))
		in {
			lists.delete(pos1);
			lists.delete(if pos1 < pos2 then pos2 - 1 else pos2 fi);
			lists.add(l1.append(l2));
		}
	};
};