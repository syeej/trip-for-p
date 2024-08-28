package team.seventhmile.tripforp.domain.plan.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
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
        Plan plan = new Plan(LocalDate.now(), LocalDate.now().plusDays(1), "Trip to North", Area.SEOUL);
        plan.setViews(100);

        Place place = new Place();
        place.setTitle("Mountain Park");

        PlanItem planItem = new PlanItem();
        planItem.setSequence(1);
        planItem.setPlace(place);
        planItem.setPlan(plan);

        plan.setPlanItems(List.of(planItem));

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
        assertEquals("Mountain Park", foundPlan.getPlanItems().get(0).getPlace().getTitle());
    }
}
