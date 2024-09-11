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
  private final ObjectMapper objectMapper;

  public AlanApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  public AlanApiResponse processAlanApiRequest(
      String content,
      String clientId,
      String startDate,
      String endDate,
      String interests,
      String budget) {
    String queryContent =
        URLEncoder.encode(
            "여행 날짜: "
                + startDate
                + "부터 "
                + endDate
                + "까지, "
                + "관심사: "
                + (interests != null ? interests : "관심사 없음")
                + ", "
                + "예산: "
                + (budget != null ? budget : "제한 없음")
                + ". "
                + content
                + " 여행 코스를 추천해주세요.",
            StandardCharsets.UTF_8);

    String url =
        UriComponentsBuilder.fromHttpUrl(
                "https://kdt-api-function.azurewebsites.net" + "/api/v1/question")
            .queryParam("content", queryContent)
            .queryParam("client_id", clientId)
            .encode()
            .toUriString();

    return restTemplate.getForObject(url, AlanApiResponse.class);
  }
}
