package utils.handler;

public class PlainInvoker<T> implements Invoker<T> {
	
	public PlainInvoker(T item) {
		data = item;
	}

	@Override
	public void invoke(Handler<T> handle) {
		handle.handle(data);
	}
	
	private T data;

}
