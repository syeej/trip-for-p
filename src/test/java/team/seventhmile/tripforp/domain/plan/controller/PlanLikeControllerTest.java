package team.seventhmile.tripforp.domain.plan.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.PlanLike;
import team.seventhmile.tripforp.domain.plan.service.PlanLikeService;
import team.seventhmile.tripforp.domain.user.entity.User;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(PlanLikeController.class)
public class PlanLikeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PlanLikeService planLikeService;

  @Test
  public void likePlan_ReturnCreated() throws Exception {
    PlanLike mockPlanLike = new PlanLike();
    Mockito.when(planLikeService.likePlan(any(User.class), any(Plan.class))).thenReturn(mockPlanLike);

    String requestBody = "{ " +
            "\"user\": { " +
            "\"email\": \"test@example.com\", " +
            "\"password\": \"password\", " +
            "\"nickname\": \"tester\", " +
            "\"role\": \"USER\" }, " +
            "\"plan\": { " +
            "\"startDate\": \"2024-01-01\", " +
            "\"endDate\": \"2024-01-07\", " +
            "\"title\": \"Trip\", " +
            "\"area\": \"SEOUL\" }" +
            "}";

    mockMvc.perform(post("/api/plan-likes")
                    .contentType("application/json")
                    .content(requestBody))
            .andExpect(status().isCreated())
            .andDo(print());
  }

  @Test
  public void unlikePlan_ReturnNoContent() throws Exception {
    mockMvc.perform(delete("/api/plan-likes/{likeId}", 1L))
            .andExpect(status().isNoContent())  // HTTP 204 No Content 상태를 기대
            .andDo(print());
  }
}