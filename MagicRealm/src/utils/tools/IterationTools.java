package utils.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IterationTools {
	
	public static <V, T> List<T> map(Collection<V> lst, Function<V, T> fe) {
		ArrayList<T> ret = new ArrayList<T>(lst.size());
		map(lst, fe, ret);
		return ret;
	}
	
	public static <V, T> List<T> map(Iterable<V> iter, Function<V, T> fe) {
		ArrayList<T> ret = new ArrayList<T>();
		map(iter, fe, ret);
		return ret;
	}
	
	public static <V, T> void map(Iterable<V> iter, Function<V, T> fe, List<T> dest) {
		for(V val: iter) {
			dest.add(fe.apply(val));
		}
	}
	
	public static <V> void map(Iterable<V> iter, ForEach<V> fe) {
		for(V val: iter) {
			fe.apply(val);
		}
	}
	
	public static <T> List<T> filter(Collection<T> lst, Function<T, Boolean> filt) {
		ArrayList<T> ret = new ArrayList<T>(lst.size());
		filter(lst, filt, ret);
		return ret;
	}
	
	public static <T> List<T> filter(Iterable<T> iter, Function<T, Boolean> filt) {
		ArrayList<T> ret = new ArrayList<T>();
		filter(iter, filt, ret);
		return ret;
	}
	
	public static <T> void filter(Iterable<T> iter, Function<T, Boolean> filt, List<T> dest) {
		for(T item: iter) {
			if(filt.apply(item)) {
				dest.add(item);
			}
		}
	}
	
	public static <T> List<T> notNull(T[] items) {
		ArrayList<T> ret = new ArrayList<T>(items.length);
		for(T it: items) {
			if(it != null) {
				ret.add(it);
			}
		}
		return ret;
	}
	
	public static <T> Iterable<T> notNull(Iterable<T> items) {
		return filter(items, new Function<T, Boolean>() {
			@Override
			public Boolean apply(Object value) {
				return value != null;
			}
		});
	}

}
