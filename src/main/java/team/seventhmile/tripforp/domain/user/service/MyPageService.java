package team.seventhmile.tripforp.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.user.dto.UserInfoRequest;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //개인정보 조회
    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(UserDetails userDetails){
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(()-> new AuthCustomException(ErrorCode.USER_NOT_FOUND));
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

        return userService.resetPassword(username, newPassword);
    }

    private String extractAccessToken(HttpServletRequest request) {
        return request.getHeader("access");

    //개인정보 수정
    @Transactional
    public UserInfoResponse updateInfo(UserDetails userDetails, UserInfoRequest userInfoReq){
        User updatedUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(()-> new AuthCustomException(ErrorCode.USER_NOT_FOUND));
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
