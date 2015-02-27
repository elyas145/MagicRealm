package utils.logger;



public class ConsoleLogger
		extends ThreadLogger {

	
	// public ThreadLogger override
	
	@Override
	protected void write(String str) {
		System.out.println(str);
	}
	
	@Override
	public void start() {
		super.start();
		log("logger.ConsoleLogger: ConsoleLogger thread started");
	}
	
	
}
