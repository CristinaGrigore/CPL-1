(*******************************
 *** Classes Product-related ***
 *******************************)
class Product {
	name : String;
	model : String;
	price : Int;

	init(n : String, m: String, p : Int): SELF_TYPE {{
		name <- n;
		model <- m;
		price <- p;
		self;
	}};

	getPrice(): Int { price * 119 / 100 };

	toString(): String {
		type_name().concat("(").concat(name).concat(",").concat(model)
			.concat(")")
	};
};

class Edible inherits Product {
	-- VAT tax is lower for foods
	getPrice(): Int { price * 109 / 100 };
};

class Soda inherits Edible {
	-- sugar tax is 20 bani
	getPrice(): Int { price * 109 / 100 + 20 };
};

class Coffee inherits Edible {
	-- this is technically poison for ants
	getPrice(): Int { price * 119 / 100 };
};

class Laptop inherits Product {
	-- operating system cost included
	getPrice(): Int { price * 119 / 100 + 499 };
};

class Router inherits Product { };

(****************************
 *** Classes Rank-related ***
 ****************************)
class Rank {
	name : String;

	init(n: String): SELF_TYPE {{
		name <- n;
		self;
	}};

	toString(): String {
		type_name().concat("(").concat(name).concat(")")
	};
};

class Private inherits Rank { };

class Corporal inherits Private { };

class Sergent inherits Corporal { };

class Officer inherits Sergent { };
