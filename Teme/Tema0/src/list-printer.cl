(**
 * Afiseaza fie toate listele, fie lista de la indexul dat, daca acesta este
 * specificat.
 *)
class ListPrinter inherits IO {
	const: Constants <- new Constants;
	atoi: A2I <- new A2I;
	caster: Caster <- new Caster;

	handlePrint(index: String, lists: List): IO {
		if index = const.emptyString() then
			print(1, lists)
		else
			printNth(1, atoi.a2i(index), lists)
		fi
	};

	print(index: Int, lists: List): IO {
		if not lists.isEmpty() then {
			out_int(index).out_string(": "
				.concat(caster.objectToList(lists.head()).toString()));
			print(index + 1, lists.tail());
		} else
			self
		fi
	};

	printNth(crtIndex: Int, targetIndex: Int, lists: List): IO {
		if crtIndex < targetIndex then
			printNth(crtIndex + 1, targetIndex, lists.tail())
		else
			out_string(caster.objectToList(lists.head()).toString())
		fi
	};
};
