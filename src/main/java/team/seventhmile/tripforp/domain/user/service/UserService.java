package team.seventhmile.tripforp.domain.user.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import team.seventhmile.tripforp.domain.user.dto.UserDto;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.user.AlreadyUsedEmailException;
import team.seventhmile.tripforp.global.exception.user.UserRegistrationException;
import team.seventhmile.tripforp.global.exception.user.WithdrawnUserException;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
	}

	// 닉네임 중복 체크
	public Boolean isDuplicatedNickname(String nickname) {
		return userRepository.existsByNickname(nickname);
	}

}
