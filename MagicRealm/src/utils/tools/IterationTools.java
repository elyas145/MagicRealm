package utils.tools;

import java.util.ArrayList;
import java.util.List;

public class IterationTools {
	
	public static <V, T> List<T> map(List<V> lst, ForEach<V, T> fe) {
		ArrayList<T> ret = new ArrayList<T>(lst.size());
		for(V val: lst) {
			ret.add(fe.apply(val));
		}
		return ret;
	}
	
	public static <T> Iterable<T> notNull(T[] items) {
		ArrayList<T> ret = new ArrayList<T>(items.length);
		for(T it: items) {
			if(it != null) {
				ret.add(it);
			}
		}
		return ret;
	}

}
