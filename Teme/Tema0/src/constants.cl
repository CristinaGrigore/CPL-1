(**
 * Defineste o serie de constante pentru a nu le hardcoda in celelalte fisiere.
 *)
class Constants {
	stateListen(): String { "listen" };
	stateLoad(): String { "load" };
	stateAction(): String { "action" };
	actionFilter(): String { "filterBy" };
	actionSort(): String { "sortBy" };
	actionMerge(): String { "merge" };
	actionHelp(): String { "help" };

	endLoad(): String { "END" };

	actionPrint(): String { "print" };

	typeInt(): String { "Int" };
	typeString(): String { "String" };
	typeBool(): String { "Bool" };

	valueTrue(): String { "true" };
	valueFalse(): String { "false" };

	space(): String { " " };
	emptyString(): String { "" };

	directionAscendent(): String { "ascendent" };
	directionDescendent(): String { "descendent" };
};
