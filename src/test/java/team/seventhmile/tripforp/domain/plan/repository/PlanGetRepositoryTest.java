package team.seventhmile.tripforp.domain.plan.repository;


import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanRequest;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.PlanItem;
import team.seventhmile.tripforp.domain.plan.entity.Place;
import team.seventhmile.tripforp.domain.plan.entity.Area;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlanGetRepositoryTest {
    @Autowired
    private PlanGetRepository planRepository;

    @Test
    void findByArea_returnsPlansWithCorrectArea() {
        // given
        Place place = Place.builder()
            .placeName("Mountain Park")
            .build();

        List<PlanItem> planItems = new ArrayList<>();

        PlanItem planItem = PlanItem.builder()
            .place(place)
            .sequence(1)
            .build();
        planItems.add(planItem);

        Plan plan = Plan.builder()
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(1))
            .title("Trip to North")
            .area("서울특별시")
            .planItems(planItems)
            .build();

        for (int i = 0; i < 50; i++) {
            plan.increaseViews();
        }

        planRepository.save(plan);

        // when
        List<Plan> result = planRepository.findByArea(Area.SEOUL);

        // then
        assertEquals(1, result.size());
        Plan foundPlan = result.get(0);
        assertEquals("Trip to North", foundPlan.getTitle());
        assertEquals(100, foundPlan.getViews());
        assertEquals(1, foundPlan.getPlanItems().size());
        assertEquals(1, foundPlan.getPlanItems().get(0).getSequence());
        assertEquals("Mountain Park", foundPlan.getPlanItems().get(0).getPlace().getPlaceName());
    }
}
