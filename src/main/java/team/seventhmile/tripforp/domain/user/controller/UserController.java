package team.seventhmile.tripforp.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import team.seventhmile.tripforp.domain.user.dto.ApiResponse;
import team.seventhmile.tripforp.domain.user.dto.UserDto;
import team.seventhmile.tripforp.domain.user.service.UserService;
import team.seventhmile.tripforp.global.jwt.JwtUtil;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final JwtUtil jwtUtil;

	// 회원가입
	@PostMapping("/registration")
	public ResponseEntity<?> signup(@RequestBody UserDto userDto) {

		userService.register(userDto);

		return ResponseEntity.ok(new ApiResponse("success", "회원 가입 성공했습니다."));
	}

	// 닉네임 중복체크(중복 시 true 반환)
	@GetMapping("/nickname-verification")
	public ResponseEntity<?> checkDuplicatedNickname(
		@RequestParam("nickname") String nickname) {
		boolean isDuplicated = userService.isDuplicatedNickname(nickname);
		return isDuplicated
			? ResponseEntity.status(HttpStatus.CONFLICT).body(true) : ResponseEntity.ok(false);
	}

	// refresh 토큰 재발급
	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

		return jwtUtil.reissueToken(request, response);
	}

}
