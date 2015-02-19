package utils.structures;

public class LinkedQueue<T> implements Queue<T> {
	
	
	// private Queue Link class
	
	private class Link {
		
		
		// public constructor
		
		public Link() {
			next = null;
		}
		
		
		// public members
		
		public T 	data;
		public Link next;
		
		
	}
	
	
	// public constructor
	
	public LinkedQueue() {
		head = null;
		tail = null;
		count = 0;
	}
	
	
	// public Queue overrides
	
	@Override
	public void push(T elem) {
		Link tmp = tail;
		tail = new Link();
		tail.data = elem;
		if(tmp == null) {
			head = tail;
		}
		else {
			tmp.next = tail;
		}
		++count;
	}
	
	@Override
	public T pop() throws QueueEmptyException {
		if(isEmpty()) {
			throw new QueueEmptyException();
		}
		T ret = head.data;
		head = head.next;
		if(head == null) {
			tail = null;
		}
		--count;
		return ret;
	}
	
	@Override
	public int size() {
		return count;
	}
	
	@Override
	public boolean isEmpty() {
		return head == null;
	}
	
	
	// private object members
	
	private Link 	head;
	
	private Link	tail;
	
	private int		count;
	

}