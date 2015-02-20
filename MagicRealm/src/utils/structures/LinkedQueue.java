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
		clear();
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
	public T top() throws QueueEmptyException {
		if(isEmpty()) {
			throw new QueueEmptyException();
		}
		return head.data;
	}
	
	@Override
	public int size() {
		return count;
	}
	
	@Override
	public boolean isEmpty() {
		return head == null;
	}
	
	@Override
	public void clear() {
		head = tail = null;
		count = 0;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("LinkedQueue: ");
		Link cur = head;
		while(cur != null) {
			str.append(cur.data);
			if(cur.next != null) {
				str.append(" -> ");
			}
			cur = cur.next;
		}
		return str.toString();
	}
	
	
	// private object members
	
	private Link 	head;
	
	private Link	tail;
	
	private int		count;
	

}