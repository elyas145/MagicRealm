package utils.structures;

public interface Queue<T> {
	
	public
		void	push(T elem);
	
	public
		T		pop()
				throws QueueEmptyException;
	
	public
		int		size();
	
	public
		boolean	isEmpty();

}
