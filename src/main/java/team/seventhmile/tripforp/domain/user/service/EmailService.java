package team.seventhmile.tripforp.domain.user.service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final RedisTemplate<String, Object> redisTemplate;

    //이메일  코드 전송(회원가입시)
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

        emailGenerateAndSend(recipient);
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
    }

    //비밀번호 찾기 시 발송되는 인증메일
    public void sendPasswordResetEmail(String email) {

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getIsDeleted()) { //탈퇴
                throw new AuthCustomException(ErrorCode.WITHDRAWN_USER);
            }
        } else {
            throw new AuthCustomException(ErrorCode.USER_NOT_FOUND_IN_DATABASE);
        }

        emailGenerateAndSend(email);
    }

    // 이메일 인증 코드 생성 및 발송
    private void emailGenerateAndSend(String email){
        String emailCode = generateEmailCode().trim();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email); //수신자 설정
        message.setSubject("이메일 인증"); //제목 설정
        message.setText("귀하의 인증 코드는 " + emailCode + "입니다.\n인증 코드는 5분 간 유지됩니다."); //내용 설정
        try {
            javaMailSender.send(message);
            redisTemplate.opsForValue().set("EMAIL_CODE:" + email, emailCode, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("이메일 전송 중 오류 발생", e);
            throw new AuthCustomException(ErrorCode.EMAIL_SEND_ERROR);
        }
    }
}
