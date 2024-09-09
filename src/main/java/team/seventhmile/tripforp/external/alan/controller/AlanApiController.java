package team.seventhmile.tripforp.external.alan.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @GetMapping("area")
    public AlanApiResponse processArea(@RequestParam(name = "client_id") String clientId,
        @Valid @RequestBody AreaRecsRequest request) {

        return alanApiService.getRecommendationsByArea(clientId, request);
    }
}
