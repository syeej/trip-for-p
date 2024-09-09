package team.seventhmile.tripforp.external.alan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Service;
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

    public AlanApiResponse getRecommendationsByArea(String clientId,
        AreaRecsRequest request) {
        String area = request.getArea();
        String startDate = request.getStartDate().toString();
        String endDate = request.getEndDate().toString();
        
        String content = URLEncoder.encode(
            startDate + "~" + endDate + "까지 " + area + " 지역 기반의 특별한 여행지 또는 체험, 축제 추천해줘",
            StandardCharsets.UTF_8);
        String url = UriComponentsBuilder.fromHttpUrl(
                "https://kdt-api-function.azurewebsites.net" + "/api/v1/question")
            .queryParam("content", content)
            .queryParam("client_id", clientId)
            .encode()
            .toUriString();
        return restTemplate.getForObject(url, AlanApiResponse.class);

    }
}
