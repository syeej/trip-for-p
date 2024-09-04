package team.seventhmile.tripforp.global.exception;

public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(Class<?> resourceEntity) {
        super(String.format("User is not authorized to access this %s", resourceEntity.getSimpleName()));
    }
}
