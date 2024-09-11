package team.seventhmile.tripforp.external.alan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import team.seventhmile.tripforp.domain.plan.dto.AranPlanDto;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.repository.PlanRepository;
import team.seventhmile.tripforp.domain.user.dto.UserIdResponse;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.external.alan.dto.AlanApiResponse;
import team.seventhmile.tripforp.external.alan.dto.AreaRecsRequest;
import team.seventhmile.tripforp.global.exception.AuthCustomException;
import team.seventhmile.tripforp.global.exception.ErrorCode;

@Service
public class AlanApiService {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AlanApiService(RestTemplate restTemplate,
        ObjectMapper objectMapper,PlanRepository planRepository,UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
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

    public String getRecommendationsByArea(String clientId,
        AreaRecsRequest request) {
        String area = request.getArea();

        String totalDate = request.getStartDate().toString() + " - " + request.getEndDate().toString();

        String content = URLEncoder.encode(
                totalDate +
                "이 기간동안 날씨와 " +
                area + " 지역 안에서 여행기간동안 열리는 체험행사와 축제를 알려주고 여행지를 추천해줘.",
            StandardCharsets.UTF_8);
        System.out.println("content: "+content);
        String url = UriComponentsBuilder.fromHttpUrl(
                "https://kdt-api-function.azurewebsites.net" + "/api/v1/question")
            .queryParam("content", content)
            .queryParam("client_id", clientId)
            .encode()
            .toUriString();

        try {
            String getContent = Objects.requireNonNull(
                restTemplate.getForObject(url, AlanApiResponse.class)).getContent();

            return getContent;
            //return convertToHtml(getContent);
        } catch (ResourceAccessException e) {
            // 네트워크 또는 접근 실패 시 처리
            return "해당 지역 추천 여행지를 불러오는 데에 실패했습니다.: 네트워크 오류";
        } catch (NullPointerException e) {
            // 응답이 null일 경우 처리
            return "해당 지역 추천 여행지를 불러오는 데에 실패했습니다.: 데이터 없음";
        } catch (Exception e) {
            // 그 외 모든 예외 처리
            return "해당 지역 추천 여행지를 불러오는 데에 실패했습니다.";
        }

    }

    // 사용할지 고민해봐야함
    public String convertToHtml(String text) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>\n");

        // 텍스트를 줄 단위로 분할
        String[] lines = text.split("\n");

        for (String line : lines) {
            if (line.startsWith("###")) {
                // ### 헤더를 h3 태그로 변환
                html.append("<h3>").append(line.substring(4)).append("</h3>\n");
            } else if (line.startsWith("##")) {
                // ## 헤더를 h2 태그로 변환
                html.append("<h2>").append(line.substring(3)).append("</h2>\n");
            } else if (line.matches("^\\d+\\.\\s.*")) {
                // 번호 매긴 목록을 <ol> 태그로 변환
                if (!html.toString().endsWith("</ol>\n")) {
                    html.append("<ol>\n");
                }
                html.append("<li>").append(line.replaceFirst("^\\d+\\.\\s", "")).append("</li>\n");
            } else if (!line.trim().isEmpty()) {
                // <ol>이 열려있다면 닫기
                if (html.toString().endsWith("</li>\n")) {
                    html.append("</ol>\n");
                }
                // 일반 텍스트를 <p> 태그로 변환
                html.append("<p>").append(line).append("</p>\n");
            }
        }

        // <ol>이 아직 열려있다면 닫기
        if (html.toString().endsWith("</li>\n")) {
            html.append("</ol>\n");
        }

        // **굵은 텍스트**를 <strong> 태그로 변환
        Pattern boldPattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
        Matcher boldMatcher = boldPattern.matcher(html);
        html = new StringBuilder(boldMatcher.replaceAll("<strong>$1</strong>"));

        html.append("</body></html>");
        return html.toString();
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
}
