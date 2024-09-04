package team.seventhmile.tripforp.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "400001", "잘못된 인증 코드입니다."),
    WITHDRAWN_USER(HttpStatus.FORBIDDEN, "403002", "탈퇴한 사용자입니다."),
    VERIFICATION_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "404001", "인증 코드를 찾을 수 없습니다."),
    EMAIL_ALREADY_IN_USE(HttpStatus.CONFLICT, "409001", "이미 사용 중인 이메일입니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "403001", "이메일 인증이 완료되지 않았습니다."),
    EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500001", "이메일 전송 중 오류가 발생했습니다."),
    REDIS_CONNECTION_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "503001", "일시적인 Redis 연결 오류");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
