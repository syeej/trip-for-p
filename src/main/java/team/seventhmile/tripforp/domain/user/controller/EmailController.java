package team.seventhmile.tripforp.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.seventhmile.tripforp.domain.user.dto.EmailCodeRequest;
import team.seventhmile.tripforp.domain.user.service.EmailService;

@Slf4j
@RestController
@RequestMapping("/api/mails")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerificationEmail(@RequestBody EmailCodeRequest emailCodeReq) {
        log.info("send-verification-email :{}", emailCodeReq.getEmail());
        emailService.sendVerificationEmail(emailCodeReq.getEmail());
        return ResponseEntity.ok("인증 이메일이 전송되었습니다.");
    }

    @PostMapping("/verification")
    public ResponseEntity<String> verifyEmail(@RequestBody EmailCodeRequest emailCodeReq) {
        log.info("인증 이메일 :{}", emailCodeReq.getEmail());
        log.info("인증 코드 :{}", emailCodeReq.getCode());
        boolean isVerified = emailService.verifyEmailCode(emailCodeReq.getEmail(), emailCodeReq.getCode());
        if (isVerified) {
            return ResponseEntity.ok("이메일이 성공적으로 인증되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("인증 코드가 올바르지 않습니다.");
        }
    }
}
