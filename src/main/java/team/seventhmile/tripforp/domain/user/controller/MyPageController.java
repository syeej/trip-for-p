package team.seventhmile.tripforp.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.domain.user.dto.ModifyPasswordRequest;
import org.springframework.web.bind.annotation.*;
import team.seventhmile.tripforp.domain.user.dto.UserInfoRequest;
import team.seventhmile.tripforp.domain.user.dto.UserInfoResponse;
import team.seventhmile.tripforp.domain.user.service.MyPageService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mypage")
public class MyPageController {
    private final MyPageService myPageService;

    //(마이페이지)개인정보 조회
    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> getUserInfo(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(myPageService.getUserInfo(userDetails));
    }

    //비밀번호 
    @PatchMapping("/info/password")
    public ResponseEntity<?> modifyPassword(HttpServletRequest request,
        @RequestBody ModifyPasswordRequest modifyPasswordRequest) {
        return myPageService.modifyPassword(request, modifyPasswordRequest.getNewPassword());

    //개인정보 수정
    @PatchMapping("/info")
    public ResponseEntity<UserInfoResponse> updateUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserInfoRequest userInfoReq){

        return ResponseEntity.ok(myPageService.updateInfo(userDetails, userInfoReq));
    }
}
