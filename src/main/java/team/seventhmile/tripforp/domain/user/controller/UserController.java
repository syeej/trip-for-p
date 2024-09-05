package team.seventhmile.tripforp.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.domain.user.dto.ApiResponse;
import team.seventhmile.tripforp.domain.user.dto.ModifyPasswordRequest;
import team.seventhmile.tripforp.domain.user.dto.UserDto;
import team.seventhmile.tripforp.domain.user.service.UserService;
import team.seventhmile.tripforp.global.exception.AuthCustomException;
import team.seventhmile.tripforp.global.exception.ErrorCode;
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
	public ResponseEntity<?> checkDuplicatedNickname(@RequestParam("nickname") String nickname) {
		boolean isDuplicated = userService.isDuplicatedNickname(nickname);
		if (isDuplicated) {
			throw new AuthCustomException(ErrorCode.NICKNAME_ALREADY_IN_USE);
		} else {
			return ResponseEntity.ok(new ApiResponse("success", "사용 가능한 닉네임입니다."));
		}
	}

	// refresh 토큰 재발급
	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

		return jwtUtil.reissueToken(request, response);
	}

	@PatchMapping("/password/renewal")
	public ResponseEntity<?> modifyPassword(HttpServletRequest request,
		@RequestBody ModifyPasswordRequest modifyPasswordRequest) {

		return userService.modifyPassword(request, modifyPasswordRequest.getNewPassword());
	}

}
