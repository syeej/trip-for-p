package team.seventhmile.tripforp.global.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Class<?> resourceEntity) {
        super(String.format("%s not found", resourceEntity.getSimpleName()));
    }

    public ResourceNotFoundException(Class<?> resourceEntity, Long resourceId) {
        super(String.format("%s not found with id : %s", resourceEntity.getSimpleName(), resourceId));
    }
}
