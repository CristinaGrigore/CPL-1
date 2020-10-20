(*******************************
 *** Classes Product-related ***
 *******************************)
class Product {
	name : String;
	model : String;
	price : Int;

	init(n : String, m: String, p : Int):SELF_TYPE {{
		name <- n;
		model <- m;
		price <- p;
		self;
	}};

	getprice(): Int { price * 119 / 100 };

	toString(): String {
		type_name().concat("(").concat(name).concat(",").concat(model)
			.concat(")")
	};
};

class Edible inherits Product {
	-- VAT tax is lower for foods
	getprice(): Int { price * 109 / 100 };
};

class Soda inherits Edible {
	-- sugar tax is 20 bani
	getprice(): Int { price * 109 / 100 + 20 };
};

class Coffee inherits Edible {
	-- this is technically poison for ants
	getprice(): Int { price * 119 / 100 };
};

class Laptop inherits Product {
	-- operating system cost included
	getprice(): Int { price * 119 / 100 + 499 };
};

class Router inherits Product { };

(****************************
 *** Classes Rank-related ***
 ****************************)
class Rank {
	name : String;

	init(n: String): String {
		name <- n
	};

	toString(): String {
		-- Hint: what are the default methods of Object?
		type_name().concat("(").concat(name).concat(")")
	};
};

class Private inherits Rank { };

class Corporal inherits Private { };

class Sergent inherits Corporal { };

class Officer inherits Sergent { };