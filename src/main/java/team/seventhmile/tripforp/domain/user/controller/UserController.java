package team.seventhmile.tripforp.domain.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import team.seventhmile.tripforp.domain.user.dto.UserDto;
import team.seventhmile.tripforp.domain.user.service.UserService;
import team.seventhmile.tripforp.global.jwt.JwtUtil;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final JwtUtil jwtUtil;

	// 회원가입
	@PostMapping("/registration")
	public ResponseEntity<String> signup(@RequestBody UserDto userDto) {

		userService.register(userDto);

		return ResponseEntity.ok("User registered successfully");
	}

	// 닉네임 중복체크(중복 시 true 반환)
	@GetMapping("/nickname-verification")
	public ResponseEntity<Boolean> checkDuplicatedNickname(
		@RequestParam("nickname") String nickname) {
		boolean isDuplicated = userService.isDuplicatedNickname(nickname);
		return isDuplicated
			? ResponseEntity.status(HttpStatus.CONFLICT).body(true) : ResponseEntity.ok(false);
	}

	// refresh 토큰 재발급
	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request) {
		String refreshToken = extractRefreshTokenFromCookie(request);
		return jwtUtil.reissueAccessToken(refreshToken);
	}

	private String extractRefreshTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("refresh")) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}
