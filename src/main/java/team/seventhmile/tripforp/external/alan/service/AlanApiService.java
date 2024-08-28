package team.seventhmile.tripforp.external.alan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import team.seventhmile.tripforp.external.alan.dto.AlanApiResponse;

@Service
public class AlanApiService {

    private final RestTemplate restTemplate;
    private final String alanApiUrl;
    private final ObjectMapper objectMapper;

    public AlanApiService(RestTemplate restTemplate,
        ObjectMapper objectMapper,
        @Value("${alan.api.url}") String alanApiUrl) {
        this.restTemplate = restTemplate;
        this.alanApiUrl = alanApiUrl;
        this.objectMapper = objectMapper;
    }

    public AlanApiResponse processAlanApiRequest(String content, String clientId) {
        content = URLEncoder.encode("2024년 9월 17일부터 2024년 9월 19일까지 " + content + " 여행 코스(숙소, 음식점, 관광지)를 추천해줘", StandardCharsets.UTF_8);
        String url = UriComponentsBuilder.fromHttpUrl(alanApiUrl + "/api/v1/question")
            .queryParam("content", content)
            .queryParam("client_id", clientId)
            .encode()
            .toUriString();
        return restTemplate.getForObject(url, AlanApiResponse.class);

    }
}
