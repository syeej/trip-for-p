package team.seventhmile.tripforp.external.alan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import team.seventhmile.tripforp.domain.plan.dto.AranPlanDto;
import team.seventhmile.tripforp.domain.plan.dto.GetPlanListResponse;
import team.seventhmile.tripforp.domain.plan.dto.GetPlanResponse;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetItemDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanItemDto;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.PlanItem;
import team.seventhmile.tripforp.domain.plan.repository.PlanRepository;
import team.seventhmile.tripforp.domain.plan.service.PlanLikeService;
import team.seventhmile.tripforp.domain.plan.service.PlanService;
import team.seventhmile.tripforp.domain.user.dto.UserIdResponse;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.external.alan.dto.AlanApiResponse;
import team.seventhmile.tripforp.global.exception.AuthCustomException;
import team.seventhmile.tripforp.global.exception.ErrorCode;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;

@Service
public class AlanApiService {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final PlanService planService;
    private final PlanLikeService planLikeService;

    public AlanApiService(RestTemplate restTemplate,
        ObjectMapper objectMapper,PlanRepository planRepository,UserRepository userRepository,
        PlanService planService, PlanLikeService planLikeService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
        this.planService = planService;
        this.planLikeService = planLikeService;
    }

    public AlanApiResponse processAlanApiRequest(String content, String clientId) {
        content = URLEncoder.encode("2024년 9월 17일부터 2024년 9월 19일까지 " + content + " 여행 코스(숙소, 음식점, 관광지)를 추천해줘", StandardCharsets.UTF_8);
        String url = UriComponentsBuilder.fromHttpUrl("https://kdt-api-function.azurewebsites.net" + "/api/v1/question")
            .queryParam("content", content)
            .queryParam("client_id", clientId)
            .encode()
            .toUriString();
        return restTemplate.getForObject(url, AlanApiResponse.class);

    }

    //개인맞춤형 ai 여행코스추천서비스
    public AlanApiResponse userprocessAlanApiRequest(String clientId,UserDetails userDetails) {
        UserIdResponse userIdResponse = userRepository.findUserIdByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AuthCustomException(ErrorCode.USER_NOT_FOUND));
        List<Object[]> planAndPlaceData = planRepository.findPlansAndPlacesByUserId(userIdResponse.getId());
        List<String> placeNames = new ArrayList<>();
        String planTitle = null;
        String planArea = null;
        for (Object[] data : planAndPlaceData) {
            planTitle = (String) data[0];
            // data[1]을 Area enum으로 변환 후 name을 가져옵니다.
            planArea = ((Area) data[1]).getName();
            placeNames.add((String) data[2]);
        }

        // PlanLikes 정보 가져오기
        List<Object[]> likedPlansData = planRepository.findLikedPlansByUserId(userIdResponse.getId());
        List<String> likedPlanTitles = new ArrayList<>();
        List<String> likedPlanAreas = new ArrayList<>();
        for (Object[] data : likedPlansData) {
            likedPlanTitles.add((String) data[0]);
            likedPlanAreas.add((String) data[1]);
        }

        AranPlanDto aranPlanDto = new AranPlanDto(planTitle, planArea, placeNames, likedPlanTitles, likedPlanAreas);
        // Plan, place, and liked plan data를 포함한 content 생성
        StringBuilder contentBuilder = new StringBuilder();
        // 현재 여행 계획 정보 추가
        contentBuilder.append("내가 여행한 코스: ").append(aranPlanDto.getPlanTitle()).append("\n")
                .append("내가 여행한 지역: ").append(aranPlanDto.getPlanArea()).append("\n");

        // PlanItems의 장소 정보 추가
        contentBuilder.append("내가 방문한 장소:\n");
        aranPlanDto.getPlaceNames().forEach(place -> contentBuilder.append("- ").append(place).append("\n"));

        // 좋아요한 여행 코스 정보 추가
        contentBuilder.append("좋아요를 누른 여행 코스:\n");
        for (int i = 0; i < aranPlanDto.getLikedPlanTitles().size(); i++) {
            contentBuilder.append("- 제목: ").append(aranPlanDto.getLikedPlanTitles().get(i))
                    .append(", 지역: ").append(aranPlanDto.getLikedPlanAreas().get(i))
                    .append("\n");
        }
        contentBuilder.append("위의 내용을 바탕으로 사용자의 관심사, 여행 패턴, 리뷰, 방문 이력등을 분석하여 맞춤형 여행 코스를 추천해줘");
        System.out.println("Generated Content: \n" + contentBuilder.toString());
        // 최종 content에 대해 URL 인코딩
        String finalContent = URLEncoder.encode(contentBuilder.toString(), StandardCharsets.UTF_8);
        // Alan API 호출 URL 생성
        String url = UriComponentsBuilder.fromHttpUrl("https://kdt-api-function.azurewebsites.net" + "/api/v1/question")
                .queryParam("content", finalContent)
                .queryParam("client_id", clientId)
                .encode()
                .toUriString();

        // API 호출 및 응답 처리
        return restTemplate.getForObject(url, AlanApiResponse.class);
    }

    //개인맞춤형 ai 여행코스추천서비스
    public AlanApiResponse userprocessAlanApiRequestV2(String clientId,UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException(User.class));

        Set<String> places = new HashSet<>();
        List<String> areas = new ArrayList<>();
        // 사용자가 등록한 여행 코스 최신 10개
        Page<GetPlanListResponse> myPlanList = planService.getMyPlanList(userDetails,
            PageRequest.of(0, 10));
        for (GetPlanListResponse plan : myPlanList) {
            areas.add(plan.getArea());
//            GetPlanResponse myPlan = planService.getPlanById(plan.getId());
//            for (PlanGetItemDto planItem : myPlan.getPlanItems()) {
//                places.add(planItem.getPlace().getPlaceName());
//            }
        }
        // 사용자가 좋아요한 여행 코스 최신 10개
        Page<GetPlanListResponse> myLikePlanList = planLikeService.getMyFavPlanList(userDetails,
            PageRequest.of(0, 10));
        for (GetPlanListResponse plan : myLikePlanList) {
            areas.add(plan.getArea());
//            GetPlanResponse myPlan = planService.getPlanById(plan.getId());
//            for (PlanGetItemDto planItem : myPlan.getPlanItems()) {
//                places.add(planItem.getPlace().getPlaceName());
//            }
        }

        StringBuilder contentBuilder = new StringBuilder();
        for (String area : areas) {
            contentBuilder.append(area).append(", ");
        }
        if (contentBuilder.isEmpty()) {
            return new AlanApiResponse(null, "사용자 데이터가 부족합니다.");
        }

        contentBuilder.append("을 [지역명]이라고 했을 때 [지역명]의 빈도들을 분석해서 해당 [지역명]의 유명한 장소들로 여행 코스를 추천해주세요.단, 다음 JSON 형식으로 응답:\n");
        contentBuilder.append("```json\n");
        contentBuilder.append("{\n");
        contentBuilder.append("  \"[지역명]\": [\n");
        contentBuilder.append("    {\n");
        contentBuilder.append("      \"명소\": \"[명소 이름]\",\n");
        contentBuilder.append("      \"설명\": \"[명소에 대한 간단한 설명]\"\n");
        contentBuilder.append("    },\n");
        contentBuilder.append("    {\n");
        contentBuilder.append("      \"명소\": \"[명소 이름]\",\n");
        contentBuilder.append("      \"설명\": \"[명소에 대한 간단한 설명]\"\n");
        contentBuilder.append("    }\n");
        contentBuilder.append("  ],\n");
        contentBuilder.append("  \"[다른 지역명]\": [\n");
        contentBuilder.append("    {\n");
        contentBuilder.append("      \"명소\": \"[명소 이름]\",\n");
        contentBuilder.append("      \"설명\": \"[명소에 대한 간단한 설명]\"\n");
        contentBuilder.append("    }\n");
        contentBuilder.append("  ]\n");
        contentBuilder.append("}\n");
        contentBuilder.append("```\n");

        System.out.println("Generated Content: \n" + contentBuilder.toString());
        // 최종 content에 대해 URL 인코딩
        String finalContent = URLEncoder.encode(contentBuilder.toString(), StandardCharsets.UTF_8);
        // Alan API 호출 URL 생성
        String url = UriComponentsBuilder.fromHttpUrl("https://kdt-api-function.azurewebsites.net" + "/api/v1/question")
            .queryParam("content", finalContent)
            .queryParam("client_id", clientId)
            .encode()
            .toUriString();

        // API 호출 및 응답 처리
        return restTemplate.getForObject(url, AlanApiResponse.class);
    }
}
