package team.seventhmile.tripforp.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "E001", "이메일 인증이 완료되지 않았습니다."),
    REDIS_CONNECTION_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "E002", "일시적인 Redis 연결 오류");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
