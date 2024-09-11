package team.seventhmile.tripforp.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.domain.user.dto.ApiResponse;
import team.seventhmile.tripforp.domain.user.dto.EmailCodeRequest;
import team.seventhmile.tripforp.domain.user.service.EmailService;

@Slf4j
@RestController
@RequestMapping("/api/mails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerificationEmail(@Valid @RequestBody EmailCodeRequest emailCodeReq) {
        log.info("send-verification-email :{}", emailCodeReq.getEmail());
        emailService.sendVerificationEmail(emailCodeReq.getEmail());
        return ResponseEntity.ok(new ApiResponse("success", "인증 이메일이 전송되었습니다."));
    }

    @PostMapping("/verification")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody EmailCodeRequest emailCodeReq) {
        log.info("인증 이메일 :{}", emailCodeReq.getEmail());
        log.info("인증 코드 :{}", emailCodeReq.getCode());
        emailService.verifyEmailCode(emailCodeReq.getEmail(), emailCodeReq.getCode());
        return ResponseEntity.ok(new ApiResponse("success", "이메일이 성공적으로 인증되었습니다."));
    }

    //비밀번호 찾기 시 발송되는 인증메일
    @PostMapping("/password-reset-request")
    public ResponseEntity<?> sendPasswordResetEmail(@Valid @RequestBody EmailCodeRequest emailCodeReq) {
        log.info("send-password-reset-request-email :{}", emailCodeReq.getEmail());
        emailService.sendPasswordResetEmail(emailCodeReq.getEmail());
        return ResponseEntity.ok(new ApiResponse("success", "인증 이메일이 전송되었습니다."));
    }
}
