//package team.seventhmile.tripforp.domain.plan.controller;
//
//import java.time.LocalDate;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import team.seventhmile.tripforp.domain.plan.dto.PlaceDto;
//import team.seventhmile.tripforp.domain.plan.dto.PlanGetDetailDto;
//import team.seventhmile.tripforp.domain.plan.dto.PlanGetDto;
//import team.seventhmile.tripforp.domain.plan.dto.PlanGetItemDto;
//import team.seventhmile.tripforp.domain.plan.dto.PlanListItemDto;
//import team.seventhmile.tripforp.domain.plan.entity.Area;
//import team.seventhmile.tripforp.domain.plan.entity.Place;
//import team.seventhmile.tripforp.domain.plan.service.PlanService;
//import team.seventhmile.tripforp.domain.user.entity.User;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(controllers = PlanController.class)
//public class PlanControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private PlanService planService;
//
//    @Test
//    void getPlansByArea_returnsPlanWithItems() throws Exception {
//        User mockUser;
//        mockUser = mock(User.class);
//        mockUser.setId(1L);
//        mockUser.setNickname("User");
//        PlanGetDto planResponseDto = new PlanGetDto(
//                mockUser,
//                "Trip to North", 100,
//                List.of(new PlanListItemDto(1, "Mountain Park"))
//        );
//
//        when(planService.getPlansByArea("서울특별시")).thenReturn(Collections.singletonList(planResponseDto));
//
//        mockMvc.perform(get("/api/plans")
//                        .param("area", "SEOUL")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].title").value("Trip to North"))
//                .andExpect(jsonPath("$[0].views").value(100))
//                .andExpect(jsonPath("$[0].planItems[0].sequence").value(1))
//                .andExpect(jsonPath("$[0].planItems[0].placeTitle").value("Mountain Park"));
//    }
//
//    @Test
//    void getPlan_NotFound() throws Exception {
//        when(planService.getPlanById(1L)).thenThrow(new IllegalArgumentException("Plan not found with id: 1"));
//
//        mockMvc.perform(get("/api/plans/1")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isNotFound())
//            .andExpect(jsonPath("$.message").value("Plan not found with id: 1"));
//    }
//
//    @Test
//    void getPlan_Success() throws Exception {
//        User mockUser;
//        mockUser = mock(User.class);
//        when(mockUser.getId()).thenReturn(1L);
//        when(mockUser.getNickname()).thenReturn("Test User");
//
//        // Mock the place and plan items
//        PlaceDto placeDto = new PlaceDto("충남 태안군 남면 신온리 168-14", "숙박", "비바온풀빌라펜션", "http://place.map.kakao.com/1452543531");
//        PlanGetItemDto planItemDto = new PlanGetItemDto(1L, placeDto, LocalDate.now(), "Test Memo", 1);
//
//        // Create the PlanGetDetailDto including the mocked User
//        PlanGetDetailDto planResponseDto = new PlanGetDetailDto(mockUser, 1L, "Test Plan", LocalDate.now(), LocalDate.now().plusDays(2), Area.SEOUL, 100, Collections.singletonList(planItemDto), Collections.emptyList());
//
//        // Mock the planService to return the PlanGetDetailDto
//        when(planService.getPlanById(1L)).thenReturn(planResponseDto);
//
//        // Perform the mockMvc test and verify the response
//        mockMvc.perform(get("/api/plans/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.title").value("Test Plan"))
//                .andExpect(jsonPath("$.planItems[0].place.placeName").value("비바온풀빌라펜션"))
//                .andExpect(jsonPath("$.user.name").value("Test User")); // Verify user information
//    }
//}
