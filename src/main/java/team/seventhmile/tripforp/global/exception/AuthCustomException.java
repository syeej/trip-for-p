package team.seventhmile.tripforp.global.exception;

import lombok.Getter;

@Getter
public class AuthCustomException extends RuntimeException{
    private final ErrorCode errorCode;
    private String errorMessage;

    public AuthCustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AuthCustomException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
        this.errorMessage = detailMessage;
    }

}
