package utils.handler;

public interface Invoker<T> {

	void invoke(Handler<T> handle);

}
