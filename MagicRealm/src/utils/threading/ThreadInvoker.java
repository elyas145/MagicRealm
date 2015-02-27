package utils.threading;

import java.util.concurrent.Semaphore;

import utils.handler.Handler;
import utils.handler.Invoker;
import utils.structures.LinkedQueue;
import utils.structures.Queue;
import utils.structures.QueueEmptyException;

/**
 * 
 * @author taylor
 *
 * @param <T>
 * 
 *            This class takes a handler and invokes the handler in a thread if
 *            items exist in the queue. It follows the producer consumer
 *            pattern. It acts as a consumer of T objects.
 * 
 */

public class ThreadInvoker<T> extends Threadable {

	// public constructor

	public ThreadInvoker(Handler<T> hand) {
		queued = new LinkedQueue<Invoker<T>>();
		cancelled = false;
		handler = hand;
		semaphore = new Semaphore(0); // queue is empty
	}

	// public methods

	public void add(Invoker<T> inv) {
		if (cancelled) {
			throw new RuntimeException("Thread invoker has been cancelled: "
					+ "may not add new elements");
		}
		synchronized (queued) {
			queued.push(inv);
		}
		semaphore.release();
	}

	public void cancel() {
		cancelled = true;
		stop();
	}

	@Override
	public void stop() {
		super.stop();
		semaphore.release();
	}

	// public Threadable override

	@Override
	public final void mainLoop() {
		try {
			while (isRunning()) {
				iterate();
			}
		} catch (InterruptedException e) {
		}
		if (!cancelled) {
			flushQueue();
		}
	}

	// protected setter for handler

	protected void setHandler(Handler<T> hand) {
		handler = hand;
	}

	// private methods

	private void iterate() throws InterruptedException {
		try {
			Invoker<T> invoker;
			semaphore.acquire(); // consume a handler
			if (!queued.isEmpty() && !cancelled) {
				synchronized (queued) {
					invoker = queued.pop();
				}
				invoker.invoke(handler); // invoke the handler
			}
		} catch (QueueEmptyException qee) {
			qee.printStackTrace();
		}
	}

	private void flushQueue() {
		try {
			while (!queued.isEmpty()) {
				flushItem();
			}
		} catch (QueueEmptyException qee) {
			qee.printStackTrace();
		}
	}
	
	private void flushItem() throws QueueEmptyException {
		Invoker<T> invoker;
		synchronized(queued) {
			invoker = queued.pop();
		}
		invoker.invoke(handler);
	}

	private Queue<Invoker<T>> queued;

	private boolean cancelled;

	private Handler<T> handler;

	private Semaphore semaphore;

}
