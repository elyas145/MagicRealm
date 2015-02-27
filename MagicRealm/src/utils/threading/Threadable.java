package utils.threading;

import utils.logger.Logger;


public abstract class Threadable implements Runnable {
	
	
	// public abstract methods
	
	public abstract
		void	mainLoop();
	
	
	// public constructor
	
	public Threadable() {
		running = false;
		thread = new Thread(this);
		logger = null;
	}
	
	
	// public methods
	
	public void setLogger(Logger log) {
		logger = log;
	}
	
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
			log("threading.Threadable: Waiting for thread to join: " + this);
			thread.join();
			log("threading.Threadable: Thread joined: " + this);
		} catch (InterruptedException e) {
			log("threading.Threadable: Thread join failed: " + e + " for " + this);
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
		if(logger != null) {
			logger.log(data);
		}
	}
	
	protected Logger getLogger() {
		return logger;
	}
	
	
	// private object members
	
	private boolean	running;
	
	private Thread	thread;
	
	private Logger	logger;
	
	
}
