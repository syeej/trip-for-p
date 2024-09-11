package team.seventhmile.tripforp.external.alan.controller;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.external.alan.dto.AlanApiResponse;
import team.seventhmile.tripforp.external.alan.dto.AreaRecsRequest;
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

    // 여행 지역 기반의 특별한 여행지 또는 체험 추천 서비스
    @GetMapping("/area")
    public String processArea(@RequestParam(name = "client_id") String clientId,
                              @RequestParam(name = "startDate") @NotNull @FutureOrPresent LocalDate startDate,
                              @RequestParam(name = "endDate") @NotNull @Future LocalDate endDate,
                              @RequestParam(name = "area") @Pattern(regexp = "^(서울|경기|인천|강원|충북|충남|대전|경북|경남|대구|울산|부산|전북|전남|광주|제주|세종)$") String area) {

        AreaRecsRequest request = new AreaRecsRequest(startDate, endDate, area);
        return alanApiService.getRecommendationsByArea(clientId, request);
    }

    //개인맞춤형 ai 여행코스추천서비스
    @GetMapping("/user")
    public AlanApiResponse userprocess(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "client_id", required = true) String clientId
    ) {
        return alanApiService.userprocessAlanApiRequest(clientId, userDetails);
    }

    @GetMapping("/v2/user")
    public AlanApiResponse userprocessV2(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(name = "client_id") String clientId
    ) {
        return alanApiService.userprocessAlanApiRequestV2(clientId, userDetails);
    }
}
