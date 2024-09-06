package team.seventhmile.tripforp.global.exception;

import jakarta.validation.ValidationException;
import org.springframework.util.StringUtils;

public class ValidationUtils {

	private ValidationUtils() {
	}

	public static void validateField(String field, String fieldName) {
		if (!StringUtils.hasText(field)) {
			throw new ValidationException(fieldName + " cannot be empty");
		}
	}
}
