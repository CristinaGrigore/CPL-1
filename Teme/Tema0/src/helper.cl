class Helper inherits IO {
	printHelp(): IO {{
		out_string("Stores lists of various objects and performs the following "
			.concat("actions on them:\n"));
		out_string("load: Gets items from STDIN, one per line, until the "
			.concat("string 'END' is encountered. The supported items are the ")
			.concat("following:\n"));
		out_string("\tInt <value>\n");
		out_string("\tString <value>\n");
		out_string("\tBool <value>\n");
		out_string("\tIO\n");
		out_string("\tObject\n");
		out_string("\tSoda <name> <model> <price>\n");
		out_string("\tCoffee <name> <model> <price>\n");
		out_string("\tLaptop <name> <model> <price>\n");
		out_string("\tRouter <name> <model> <price>\n");
		out_string("\tPrivate <name>\n");
		out_string("\tCorporal <name>\n");
		out_string("\tSergent <name>\n");
		out_string("\tOfficer <name>\n");
		out_string("print [index]: Print the list at the given index or all "
			.concat("of them if none is given.\n"));
		out_string("merge <index1> <index2>: Merge lists at the 2 indices, "
			.concat("remove them and add the resulting list at the end of the ")
			.concat("overall list of lists.\n"));
		out_string("sortBy <index> <comparator> <mode>: Sorts the list at the "
			.concat("given index according to the given comparator and in the ")
			.concat("way given by the mode: ascendent or descendent. The ")
			.concat("available comparators are:\n"));
		out_string("\tPriceComparator: compares items by their price.\n");
		out_string("\tRankComparator: compares ranks by their price.\n");
		out_string("\tAlphabeticComparator: compares 'String' items "
			.concat("alphabetically.\n"));
		out_string("filterBy <index> <type>: Applies the given filter type to "
			.concat("the list at the given filter. The supported filter types ")
			.concat("are:\n"));
		out_string("\tProductFilter: Filters out non-product items.\n");
		out_string("\tRankFilter: Filters out non-rank items.\n");
		out_string("\tSamePriceFilter: Keeps the products whose price is the "
			.concat("same as if it were a genric product.\n"));
	}};
};
