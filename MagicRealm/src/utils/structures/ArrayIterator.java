package utils.structures;

import java.util.Iterator;

public class ArrayIterator<T> implements Iterator<T> {
	
	
	// public constructor from array
	
	public ArrayIterator(T[] obs) {
		objects = obs;
		index = 0;
	}
	
	
	// public Iterator overrides
	
	@Override
	public boolean hasNext() {
		return index < objects.length;
	}

	@Override
	public T next() {
		return objects[index++];
	}

	@Override
	public void remove() { }
	
	
	// private object members
	
	private T[] objects;
	
	private int index;
	

}
