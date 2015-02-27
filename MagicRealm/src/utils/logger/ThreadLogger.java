package utils.logger;

import utils.handler.Handler;
import utils.handler.PlainInvoker;
import utils.threading.ThreadInvoker;

/**
 * 
 * @author taylor
 *
 *         This class ensures that calls made to write are made synchronously
 *         and in a thread.
 *
 */

public abstract class ThreadLogger implements Logger {

	// abstract method for logging

	protected abstract void write(String str);

	// public constructor

	public ThreadLogger() {
		invoker = new ThreadInvoker<String>(new Handler<String>() {
			@Override
			public void handle(String msg) {
				write(msg);
			}
		});
	}
	
	public void start() {
		invoker.start();
	}
	
	public void stop() {
		invoker.stop();
	}
	
	public void cancel() {
		invoker.cancel();
	}
	
	public void join() {
		invoker.join();
	}

	// public Logger override method

	@Override
	public <T> void log(T str) {
		invoker.add(new PlainInvoker<String>(str.toString()));
	}

	// public Handler override

	public final void handle(String str) {
		write(str);
	}

	// private members

	private ThreadInvoker<String> invoker;

}
