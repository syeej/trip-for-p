package team.seventhmile.tripforp.domain.plan.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import team.seventhmile.tripforp.domain.plan.dto.PlanLikeResponseDto;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.PlanLike;
import team.seventhmile.tripforp.domain.plan.service.PlanLikeService;
import team.seventhmile.tripforp.domain.user.entity.User;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(PlanLikeController.class)
@DisplayName("여행 코스 좋아요 컨트롤러 테스트")
public class PlanLikeControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private PlanLikeService planLikeService;

  @Test
  @DisplayName("좋아요 요청 성공 시 201 상태 코드 반환")
  public void likePlan_ReturnCreated() throws Exception {
    // PlanLikeResponseDto를 모킹하여 반환하도록 설정
    PlanLikeResponseDto mockResponseDto = new PlanLikeResponseDto(1L, 1L, 1L);
    Mockito.when(planLikeService.likePlan(any(User.class), any(Plan.class)))
            .thenReturn(mockResponseDto);

    // 테스트용 JSON 요청 바디 생성
    String requestBody =
            "{ "
                    + "\"user\": { "
                    + "\"email\": \"test@example.com\", "
                    + "\"password\": \"password\", "
                    + "\"nickname\": \"tester\", "
                    + "\"role\": \"USER\" }, "
                    + "\"plan\": { "
                    + "\"startDate\": \"2024-01-01\", "
                    + "\"endDate\": \"2024-01-07\", "
                    + "\"title\": \"Trip\", "
                    + "\"area\": \"SEOUL\" }"
                    + "}";

    // POST 요청을 수행하고, 예상되는 상태 코드가 201 Created인지 검증
    mockMvc
            .perform(post("/api/plan-likes").contentType("application/json").content(requestBody))
            .andExpect(status().isCreated()) // 201 상태 코드 반환 기대
            .andDo(print());
  }

  @Test
  @DisplayName("좋아요 취소 시 204 상태 코드 반환")
  public void unlikePlan_ReturnNoContent() throws Exception {
    // DELETE 요청을 수행하고, 예상되는 상태 코드가 204 No Content인지 검증
    mockMvc
            .perform(delete("/api/plan-likes/{likeId}", 1L))
            .andExpect(status().isNoContent()) // 204 상태 코드 반환 기대
            .andDo(print());
  }
}