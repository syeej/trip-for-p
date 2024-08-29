package team.seventhmile.tripforp.domain.plan.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import team.seventhmile.tripforp.domain.plan.dto.PlaceGetDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDetailDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanItemDto;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.service.PlanGetDetailService;

import java.time.LocalDate;
import java.util.Collections;

@WebMvcTest(controllers = PlanGetDetailController.class)
class PlanGetDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanGetDetailService planService;

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
        PlaceGetDto placeDto = new PlaceGetDto(1L, "Test Place", "Test Address", 37.123, 127.456, "123-456", null, null);
        PlanItemDto planItemDto = new PlanItemDto(1L, placeDto, LocalDate.now(), "Test Memo", 1);
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