package team.seventhmile.tripforp.external.alan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import team.seventhmile.tripforp.external.alan.dto.AlanApiResponse;
import team.seventhmile.tripforp.external.alan.dto.AreaRecsRequest;

@Service
public class AlanApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AlanApiService(RestTemplate restTemplate,
        ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
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

        String content = URLEncoder.encode("답변을 html 형식으로 한번만 출력해주면 좋겠어." +
                totalDate + "기간동안 " + area + " 지역을 여행할거야." +
                "이 기간동안 최고온도 최저온도 강수량을 한 문장으로 알려주고, " +
                area + " 지역 안에서 여행기간동안 열리는 체험행사와 축제 그리고 여행지를 추천해줘.",
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
}
