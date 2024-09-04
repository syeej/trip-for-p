package team.seventhmile.tripforp.domain.user.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import team.seventhmile.tripforp.domain.user.dto.UserDto;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.AuthCustomException;
import team.seventhmile.tripforp.global.exception.ErrorCode;
import team.seventhmile.tripforp.global.exception.user.AlreadyUsedEmailException;
import team.seventhmile.tripforp.global.exception.user.UserRegistrationException;
import team.seventhmile.tripforp.global.exception.user.WithdrawnUserException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final RedisTemplate<String, Object> redisTemplate;

	// 회원가입
	@Transactional
	public void register(UserDto userDto) {

		String email = userDto.getEmail();
		String password = userDto.getPassword();
		String nickname = userDto.getNickname();

		// 이메일 검사
		if (!StringUtils.hasText(userDto.getEmail())) {
			throw new UserRegistrationException("이메일은 필수 입력 항목입니다.");
		}

		// 이메일 중복 체크, 탈퇴된 이메일 확인
		Optional<User> existingUser = userRepository.findByEmail(email);
		if (existingUser.isPresent()) {
			User user = existingUser.get();
			// isDeleted true -> 탈퇴(삭제)한 회원
			if (user.getIsDeleted()) {
				throw new WithdrawnUserException("탈퇴한 사용자입니다.");
			} else {
				throw new AlreadyUsedEmailException("이미 사용중인 이메일입니다.");
			}
		}

		//이메일 인증 상태 확인
		Boolean isVerified = redisTemplate.opsForValue().get("EMAIL_VERIFIED:" + userDto.getEmail()) != null;
		if(!isVerified){
			log.error("이메일 인증 안됨 {}", userDto.getEmail());
			throw new AuthCustomException(ErrorCode.EMAIL_NOT_VERIFIED);
		}
		// 비밀번호 암호화
		String encodedPassword = bCryptPasswordEncoder.encode(userDto.getPassword());

		User newUser =
			User.builder()
				.email(userDto.getEmail())
				.nickname(userDto.getNickname())
				.password(encodedPassword)
				.isDeleted(false)
				.role(Role.USER)
				.build();
		userRepository.save(newUser);
		// 회원가입 후 Redis에서 인증 정보 삭제
		redisTemplate.delete("EMAIL_CODE:" + userDto.getEmail());
		redisTemplate.delete("EMAIL_VERIFIED:" + userDto.getEmail());
	}

	// 닉네임 중복 체크
	public Boolean isDuplicatedNickname(String nickname) {
		return userRepository.existsByNickname(nickname);
	}

}
