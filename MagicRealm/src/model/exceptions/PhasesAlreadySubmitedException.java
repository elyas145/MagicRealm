package model.exceptions;

public class PhasesAlreadySubmitedException extends MRException{
	private static final long serialVersionUID = 914228616011402196L;

	public PhasesAlreadySubmitedException() {
		super("You have already submitted your moves.");
	}

}
