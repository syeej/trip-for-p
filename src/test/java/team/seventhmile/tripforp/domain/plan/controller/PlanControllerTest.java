package team.seventhmile.tripforp.domain.plan.controller;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import team.seventhmile.tripforp.domain.plan.dto.PlaceDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDetailDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetItemDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanListItemDto;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Place;
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
        when(planService.getPlansByArea("서울특별시")).thenReturn(Collections.singletonList(planResponseDto));

        // when, then
        mockMvc.perform(get("/api/plans")
                        .param("area", "SEOUL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Trip to North"))
                .andExpect(jsonPath("$[0].views").value(100))
                .andExpect(jsonPath("$[0].planItems[0].sequence").value(1))
                .andExpect(jsonPath("$[0].planItems[0].placeTitle").value("Mountain Park"));
    }

    @Test
    void getPlan_NotFound() throws Exception {
        when(planService.getPlanById(1L)).thenThrow(new IllegalArgumentException("Plan not found with id: 1"));

        mockMvc.perform(get("/api/plans/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Plan not found with id: 1"));
    }

    @Test
    void getPlan_Success() throws Exception {
        PlaceDto placeDto = new PlaceDto("충남 태안군 남면 신온리 168-14", "숙박", "비바온풀빌라펜션", "http://place.map.kakao.com/1452543531");
        PlanGetItemDto planItemDto = new PlanGetItemDto(1L, placeDto, LocalDate.now(), "Test Memo", 1);
        PlanGetDetailDto planResponseDto = new PlanGetDetailDto(1L, "Test Plan", LocalDate.now(), LocalDate.now().plusDays(2), Area.SEOUL, 100, Collections.singletonList(planItemDto), Collections.emptyList());

        when(planService.getPlanById(1L)).thenReturn(planResponseDto);

        mockMvc.perform(get("/api/plans/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Test Plan"))
            .andExpect(jsonPath("$.planItems[0].place.title").value("Test Place"));
    }
}
