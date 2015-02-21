package utils.tools;

import java.util.ArrayList;
import java.util.List;

public class ItarationTools {
	
	public static <V, T> List<T> map(List<V> lst, ForEach<V, T> fe) {
		ArrayList<T> ret = new ArrayList<T>(lst.size());
		for(V val: lst) {
			ret.add(fe.apply(val));
		}
		return ret;
	}

}
