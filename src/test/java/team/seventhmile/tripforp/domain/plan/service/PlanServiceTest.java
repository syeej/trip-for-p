package team.seventhmile.tripforp.domain.plan.service;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanListItemDto;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.PlanItem;
import team.seventhmile.tripforp.domain.plan.entity.Place;
import team.seventhmile.tripforp.domain.plan.repository.PlanGetRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PlanServiceTest {
    @Mock
    private PlanGetRepository planRepository;

    @InjectMocks
    private PlanService planService;

    public PlanServiceTest() {
        MockitoAnnotations.openMocks(this);
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
        List<PlanGetDto> result = planService.getPlansByArea(Area.SEOUL);

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
}
