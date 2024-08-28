package team.seventhmile.tripforp.domain.plan.service;

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

public class PlanGetServiceTest {
    @Mock
    private PlanGetRepository planRepository;

    @InjectMocks
    private PlanGetService planService;

    public PlanGetServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPlansByArea_returnsMappedPlanResponseDtos() {
        // given
        Place place = new Place(1L, "Address 1", null, 1L, "A", "B", "C", 100, 101, 1.1, 1.2, 1, "010-0000-0000", "Mountain Park", "COPY", null, null, 0);
        PlanItem planItem = new PlanItem(1L, null, place, 1, LocalDate.now(), null);
        Plan plan = new Plan(LocalDate.now(), LocalDate.now().plusDays(1), "Trip to North", Area.SEOUL);
        plan.setPlanItems(List.of(planItem));

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
