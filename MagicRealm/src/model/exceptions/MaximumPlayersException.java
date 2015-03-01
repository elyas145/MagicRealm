package model.exceptions;

public class MaximumPlayersException extends MRException {
	
	public MaximumPlayersException() {
		super("The maximum number of players has been reached");
	}

	private static final long serialVersionUID = 550952255707061684L;

}
