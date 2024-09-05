package team.seventhmile.tripforp.domain.user.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import team.seventhmile.tripforp.domain.user.dto.UserDto;
import team.seventhmile.tripforp.domain.user.dto.UserInfoRequest;
import team.seventhmile.tripforp.domain.user.dto.UserInfoResponse;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.AuthCustomException;
import team.seventhmile.tripforp.global.exception.ErrorCode;
import team.seventhmile.tripforp.global.jwt.JwtUtil;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final RedisTemplate<String, Object> redisTemplate;
	private final JwtUtil jwtUtil;

	// 회원가입
	@Transactional
	public void register(UserDto userDto) {

		// 이메일 누락 시
		if (!StringUtils.hasText(userDto.getEmail())) {
			throw new AuthCustomException(ErrorCode.REQUIRED_FIELD_MISSING);
		}
		// 이메일 중복 체크, 탈퇴된 이메일 확인
		Optional<User> existingUser = userRepository.findByEmail(userDto.getEmail());
		if (existingUser.isPresent()) {
			User user = existingUser.get();
			if (user.getIsDeleted()) { //탈퇴
				throw new AuthCustomException(ErrorCode.WITHDRAWN_USER);
			}
			//중복
			throw new AuthCustomException(ErrorCode.EMAIL_ALREADY_IN_USE);
		}

		//이메일 인증 상태 확인
		Boolean isVerified =
			redisTemplate.opsForValue().get("EMAIL_VERIFIED:" + userDto.getEmail()) != null;
		if (!isVerified) {
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
	public boolean isDuplicatedNickname(String nickname) {
		if (userRepository.existsByNickname(nickname)) {
			log.info("Nickname '{}' 사용중", nickname);
			return true;
		}
		return false;
	}

	// 비밀번호 재설정 로직
	public ResponseEntity<?> resetPassword(String username, String newPassword) {

		try {
			User currentUser = userRepository.findByEmail(username)
				.orElseThrow(() -> new AuthCustomException(ErrorCode.USER_NOT_FOUND_IN_DATABASE));
			log.info("사용자 '{}'의 비밀번호를 변경합니다.", username);

			User updatedUser = User.builder()
				.id(currentUser.getId())
				.email(currentUser.getEmail())
				.password(bCryptPasswordEncoder.encode(newPassword))
				.isDeleted(false)
				.nickname(currentUser.getNickname())
				.role(currentUser.getRole())
				.build();

			userRepository.save(updatedUser);

			return new ResponseEntity<>("비밀번호 변경이 성공적으로 완료되었습니다.", HttpStatus.OK);
		} catch (ExpiredJwtException e) {
			throw new AuthCustomException(ErrorCode.TOKEN_EXPIRED);
		} catch (Exception e) {
			throw new AuthCustomException(ErrorCode.PASSWORD_CHANGE_ERROR);
		}
	}

	//개인정보 조회
	@Transactional(readOnly = true)
	public UserInfoResponse getUserInfo(UserDetails userDetails) {
		User user = userRepository.findByEmail(userDetails.getUsername())
			.orElseThrow(() -> new AuthCustomException(ErrorCode.USER_NOT_FOUND));
		return UserInfoResponse.from(user);
	}

	//비밀번호 변경
	@Transactional
	public ResponseEntity<?> modifyPassword(HttpServletRequest request, String newPassword) {
		String accessToken = extractAccessToken(request);
		if (accessToken == null) {
			throw new AuthCustomException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
		}

		String username = jwtUtil.getUsername(accessToken);
		if (username == null) {
			throw new AuthCustomException(ErrorCode.EMAIL_NOT_FOUND_IN_TOKEN);
		}

		return resetPassword(username, newPassword);
	}

	private String extractAccessToken(HttpServletRequest request) {
		return request.getHeader("access");
	}

	//개인정보 수정
	@Transactional
	public UserInfoResponse updateInfo(UserDetails userDetails, UserInfoRequest userInfoReq) {
		User updatedUser = userRepository.findByEmail(userDetails.getUsername())
			.orElseThrow(() -> new AuthCustomException(ErrorCode.USER_NOT_FOUND));
		// 닉네임 업데이트 (수정 요청 있는 경우)
		if (userInfoReq.getNickname() != null && !userInfoReq.getNickname().isEmpty()) {
			if (userRepository.existsByNickname(userInfoReq.getNickname())) {
				log.info("MyPage '{}' 사용 중인 Nickname", userInfoReq.getNickname());
				throw new AuthCustomException(ErrorCode.NICKNAME_ALREADY_IN_USE);
			}
			updatedUser.updateNickname(userInfoReq.getNickname());
		}
		//비밀번호 변경(수정 요청 있는 경우)
		if (userInfoReq.getPassword() != null && !userInfoReq.getPassword().isEmpty()) {
			String newPassword = bCryptPasswordEncoder.encode(userInfoReq.getPassword());
			updatedUser.updatePassword(newPassword);
		}
		return UserInfoResponse.from(updatedUser);
	}
}
