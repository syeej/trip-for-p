package team.seventhmile.tripforp.global.exception.user;

public class AlreadyUsedEmailException extends RuntimeException {

	public AlreadyUsedEmailException(String message) {
		super(message);
	}
}