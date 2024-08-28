package team.seventhmile.tripforp.domain.plan.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanListItemDto;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.service.PlanService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PlanController.class)
public class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanService planService;

    @Test
    void getPlansByArea_returnsPlanWithItems() throws Exception {
        // given
        PlanGetDto planResponseDto = new PlanGetDto(
                "Trip to North", 100,
                List.of(new PlanListItemDto(1, "Mountain Park"))
        );
        when(planService.getPlansByArea(Area.SEOUL)).thenReturn(Collections.singletonList(planResponseDto));

        // when, then
        mockMvc.perform(get("/api/plans")
                        .param("area", "SEOUL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Trip to North"))
                .andExpect(jsonPath("$[0].views").value(100))
                .andExpect(jsonPath("$[0].planItems[0].sequence").value(1))
                .andExpect(jsonPath("$[0].planItems[0].placeTitle").value("Mountain Park"));
    }
}
