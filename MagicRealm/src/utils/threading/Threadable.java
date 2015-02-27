package utils.threading;

public abstract class Threadable implements Runnable {

	// public abstract methods

	public abstract void mainLoop();

	// public constructor

	public Threadable() {
		running = false;
		thread = new Thread(this);
	}

	// public methods

	public void start() {
		startRunning();
		thread.start();
	}

	public void stop() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isStopped() {
		return !running;
	}

	public void join() {
		try {
			thread.join();
		} catch (InterruptedException e) {
		}
	}

	// public Runnable override

	@Override
	public final void run() {
		mainLoop();
		stop();
	}

	// protected methods

	protected void startRunning() {
		running = true;
	}

	protected void stopRunning() {
		running = false;
	}

	protected <V> void log(V data) {
		System.out.println(data);
	}

	// private object members

	private boolean running;

	private Thread thread;

}
