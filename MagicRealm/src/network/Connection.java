package network;

public interface Connection<S, R> extends Sender<S>, Receiver<R> {
	
	public
		void	close();
	
	public
		String	getAddress();
	
	public
		int 	getPort();
	
}
