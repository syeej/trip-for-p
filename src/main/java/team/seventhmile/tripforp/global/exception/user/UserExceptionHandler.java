package team.seventhmile.tripforp.global.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import team.seventhmile.tripforp.domain.user.controller.UserController;
import team.seventhmile.tripforp.global.exception.ErrorResponse;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;

@RestControllerAdvice(basePackageClasses = UserController.class)
public class UserExceptionHandler {

	@ExceptionHandler(UserRegistrationException.class)
	public ResponseEntity<ErrorResponse> UserRegistrationException(ResourceNotFoundException ex,
		WebRequest request) {
		ErrorResponse response = ErrorResponse.builder()
			.status(HttpStatus.BAD_REQUEST.value())
			.message(ex.getMessage())
			.path(request.getDescription(false))
			.build();
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(WithdrawnUserException.class)
	public ResponseEntity<ErrorResponse> WithdrawnUserException(ResourceNotFoundException ex,
		WebRequest request) {
		ErrorResponse response = ErrorResponse.builder()
			.status(HttpStatus.BAD_REQUEST.value())
			.message(ex.getMessage())
			.path(request.getDescription(false))
			.build();
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AlreadyUsedEmailException.class)
	public ResponseEntity<ErrorResponse> AlreadyUsedEmailException(ResourceNotFoundException ex,
		WebRequest request) {
		ErrorResponse response = ErrorResponse.builder()
			.status(HttpStatus.BAD_REQUEST.value())
			.message(ex.getMessage())
			.path(request.getDescription(false))
			.build();
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

}
