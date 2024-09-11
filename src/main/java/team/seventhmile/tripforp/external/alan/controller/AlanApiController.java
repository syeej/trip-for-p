package team.seventhmile.tripforp.external.alan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.domain.user.dto.UserIdResponse;
import team.seventhmile.tripforp.domain.user.dto.UserInfoResponse;
import team.seventhmile.tripforp.external.alan.dto.AlanApiResponse;
import team.seventhmile.tripforp.external.alan.service.AlanApiService;

@RestController
@RequestMapping("/api/alan")
@RequiredArgsConstructor
public class AlanApiController {

    private final AlanApiService alanApiService;

    @GetMapping
    public AlanApiResponse process(
        @RequestParam(name = "content") String content,
        @RequestParam(name = "client_id") String clientId
    ) {
        return alanApiService.processAlanApiRequest(content, clientId);
    }

    //개인맞춤형 ai 여행코스추천서비스
    @GetMapping("/user")
    public AlanApiResponse userprocess(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "client_id", required = true) String clientId
    ) {
        if (userDetails == null) {
            throw new IllegalArgumentException("User is not authenticated.");
        }
        return alanApiService.userprocessAlanApiRequest(clientId, userDetails);
    }

    @GetMapping("/v2/user")
    public AlanApiResponse userprocessV2(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(name = "client_id") String clientId
    ) {
        if (userDetails == null) {
            throw new IllegalArgumentException("User is not authenticated.");
        }
        return alanApiService.userprocessAlanApiRequestV2(clientId, userDetails);
    }
}
