package team.seventhmile.tripforp.domain.plan.service;

import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDetailDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetItemDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanListItemDto;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.PlanItem;
import team.seventhmile.tripforp.domain.plan.entity.Place;

import java.time.LocalDate;
import java.util.List;
import team.seventhmile.tripforp.domain.plan.entity.PlanLike;
import team.seventhmile.tripforp.domain.plan.repository.PlanRepository;
import team.seventhmile.tripforp.domain.user.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlanServiceTest {
    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanService planService;

    public PlanServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    private Plan plan;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // User 객체 생성
        User user = User.builder()
            .id(1L)
            .nickname("Test User")
            .build();

        // Plan 객체 생성
        plan = Plan.builder()
            .title("Test Plan")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(2))
            .area("서울특별시")
            .build();

        // Place 객체 생성
        Place place = Place.builder()
            .addressName("충남 태안군 남면 신온리 168-14")
            .categoryName("숙박")
            .placeName("비바온풀빌라펜션")
            .placeUrl("http://place.map.kakao.com/1452543531")
            .build();

        // PlanItem 객체 생성
        PlanItem item = PlanItem.builder()
            .place(place)
            .tripDate(LocalDate.now())
            .sequence(1)
            .build();
        plan.addPlanItem(item);

        // PlanLike 객체 생성
        PlanLike like = PlanLike.builder()
            .id(1L)
            .user(user) // User 객체 설정
            .build();
        plan.getPlanLikes().add(like);
    }

    @Test
    void getPlansByArea_returnsMappedPlanResponseDtos() {
        // given
        Place place = Place.builder()
            .addressName("충남 태안군 남면 신온리 168-14")
            .categoryName("여행")
            .placeName("비바온풀빌라펜션")
            .placeUrl("http://place.map.kakao.com/1452543531")
            .build();
        PlanItem planItem = PlanItem.builder()
            .place(place)
            .sequence(1)
            .tripDate(LocalDate.now())
            .memo("서울 여행")
            .build();

        List < PlanItem > planItems = new ArrayList<>();
        planItems.add(planItem);
        Plan plan = Plan.builder()
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(1))
            .title("Trip to North")
            .area("서울특별시")
            .build();
        plan.addPlanItems(planItems);


        when(planRepository.findByArea(Area.SEOUL)).thenReturn(List.of(plan));

        // when
        List<PlanGetDto> result = planService.getPlansByArea("서울특별시");

        // then
        assertEquals(1, result.size());
        PlanGetDto dto = result.get(0);
        assertEquals("Trip to North", dto.getTitle());
        assertEquals(0, dto.getViews());
        assertEquals(1, dto.getPlanItems().size());

        PlanListItemDto itemDto = dto.getPlanItems().get(0);
        assertEquals(1, itemDto.getSequence());
        assertEquals("Mountain Park", itemDto.getPlaceTitle());
    }

    @Test
    void getPlanById_Success() {
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        PlanGetDetailDto result = planService.getPlanById(1L);

        assertNotNull(result);
        assertEquals("Test Plan", result.getTitle());
        assertEquals(1, result.getPlanItems().size());
        assertEquals(1, result.getPlanLikes().size());

        PlanGetItemDto itemDto = result.getPlanItems().get(0);
        assertEquals("Test Place", itemDto.getPlace().getPlaceName());

        // PlanLikeDto 검증 추가
        assertEquals(1L, result.getPlanLikes().get(0).getUser().getId());

        verify(planRepository).findById(1L);
    }

    @Test
    void getPlanById_NotFound() {
        when(planRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> planService.getPlanById(1L)
        );
        assertEquals("Plan not found with id: 1", thrown.getMessage());

        verify(planRepository).findById(1L);
    }
}
