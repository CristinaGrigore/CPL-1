class Caster {
	objectToList(obj: Object): List {
		case obj of
		list: List => list;
		o: Object => new List;
		esac
	};
};
