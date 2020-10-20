class StringTokenizer {
	const: Constants <- new Constants;
	str: String;
	pos: Int;
	len: Int;

	init(string: String): SELF_TYPE {{
		pos <- 0;
		str <- string;
		len <- str.length();
		self;
	}};

	nextToken(): String {
		let token: String <- "",
			localPos: Int <- pos
		in {
			while localPos < len loop {
				if str.substr(pos, 1) = const.space() then {
					pos <- pos + 1;
					localPos <- len;
				} else {
					token <- token.concat(str.substr(pos, 1));
					pos <- pos;
					localPos <- pos;
				} fi;
			} pool;

			token;
		}
	};
};