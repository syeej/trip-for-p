package team.seventhmile.tripforp.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.AuthCustomException;
import team.seventhmile.tripforp.global.exception.ErrorCode;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final RedisTemplate<String, Object> redisTemplate;

    //이메일 인증 코드 전송(회원가입시)
    public void sendVerificationEmail(String recipient) {
        // 이메일 중복 체크, 탈퇴된 이메일 확인
        Optional<User> existingUser = userRepository.findByEmail(recipient);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getIsDeleted()) { //탈퇴
                throw new AuthCustomException(ErrorCode.WITHDRAWN_USER);
            }
            //중복
            throw new AuthCustomException(ErrorCode.EMAIL_ALREADY_IN_USE);

        }
        String emailCode = generateEmailCode();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient); //수신자 설정
        message.setSubject("이메일 인증"); //제목 설정
        message.setText("귀하의 인증 코드는 " + emailCode + " 입니다."); //내용 설정
        try {
            javaMailSender.send(message);
            redisTemplate.opsForValue().set("EMAIL_CODE:" + recipient, emailCode, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("이메일 전송 중 오류 발생", e);
            throw new AuthCustomException(ErrorCode.EMAIL_SEND_ERROR);
        }
    }

    //이메일 인증코드 생성
    public String generateEmailCode() {
        int codeLength = 6;  // 코드자리 6자리로 설정
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(codeLength);
        Random random = new Random();

        for (int i = 0; i < codeLength; i++) {
            int index = random.nextInt(chars.length());
            char randomChar = chars.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }
    //이메일 인증코드 검증(회원가입시)
    public void verifyEmailCode(String email, String code) {
        Object storedCodeObj = redisTemplate.opsForValue().get("EMAIL_CODE:" + email);
        if (storedCodeObj == null) {
            throw new AuthCustomException(ErrorCode.VERIFICATION_CODE_NOT_FOUND);
        }

        String storedCode = storedCodeObj.toString();
        if (!storedCode.equals(code)) {
            throw new AuthCustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        redisTemplate.opsForValue().set("EMAIL_VERIFIED:" + email, "true", 5, TimeUnit.MINUTES);
        redisTemplate.delete("EMAIL_CODE:" + email);
        /*String storedCode = storedCodeObj != null ? storedCodeObj.toString() : null;
        log.info("server 이메일과 인증코드 {}: {}", email, storedCode);
        log.info("client 인증코드 : {}", code);
        if (storedCode != null && storedCode.equals(code)) {
            // 인증 성공 시 이메일 인증 상태를 Redis에 저장
            redisTemplate.opsForValue().set("EMAIL_VERIFIED:" + email, "true", 5, TimeUnit.MINUTES);
            return true;
        }
        return false;*/
    }

}
