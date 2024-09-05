package team.seventhmile.tripforp.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.user.dto.UserInfoResponse;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.AuthCustomException;
import team.seventhmile.tripforp.global.exception.ErrorCode;
import team.seventhmile.tripforp.global.jwt.JwtUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    //개인정보 조회
    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(UserDetails userDetails){
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(()-> new AuthCustomException(ErrorCode.USER_NOT_FOUND));
        return UserInfoResponse.from(user);
    }

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

        return userService.resetPassword(username, newPassword);
    }

    private String extractAccessToken(HttpServletRequest request) {
        return request.getHeader("access");
    }
}
