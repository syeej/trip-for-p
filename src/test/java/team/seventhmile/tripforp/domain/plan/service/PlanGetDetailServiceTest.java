package team.seventhmile.tripforp.domain.plan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDetailDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanItemDto;
import team.seventhmile.tripforp.domain.plan.entity.*;
import team.seventhmile.tripforp.domain.plan.repository.PlanGetDetailRepository;
import team.seventhmile.tripforp.domain.user.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PlanGetDetailServiceTest {

    @InjectMocks
    private PlanGetDetailService planService;

    @Mock
    private PlanGetDetailRepository planRepository;

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
                .id(1L)
                .title("Test Plan")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .area(Area.SEOUL)
                .planItems(new ArrayList<>())
                .planLikes(new ArrayList<>())
                .build();

        // Place 객체 생성
        Place place = Place.builder()
                .placeName("Test Place")
                .mapX(37.123)
                .mapY(127.456)
                .build();

        // PlanItem 객체 생성
        PlanItem item = PlanItem.builder()
                .place(place)
                .tripDate(LocalDate.now())
                .sequence(1)
                .build();
        plan.getPlanItems().add(item);

        // PlanLike 객체 생성
        PlanLike like = PlanLike.builder()
                .id(1L)
                .user(user) // User 객체 설정
                .build();
        plan.getPlanLikes().add(like);
    }

    @Test
    void getPlanById_Success() {
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        PlanGetDetailDto result = planService.getPlanById(1L);

        assertNotNull(result);
        assertEquals("Test Plan", result.getTitle());
        assertEquals(1, result.getPlanItems().size());
        assertEquals(1, result.getPlanLikes().size());

        PlanItemDto itemDto = result.getPlanItems().get(0);
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