package team.seventhmile.tripforp.domain.plan.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Place;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.PlanItem;
import team.seventhmile.tripforp.domain.plan.entity.QPlan;
import team.seventhmile.tripforp.global.config.QuerydslConfig;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(locations = "classpath:application.yml")
@Import(QuerydslConfig.class)
public class PlanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private PlanRepository planRepository;

    private final QPlan qPlan = QPlan.plan;

    @BeforeEach
    public void setup() {
        Plan plan1 = Plan.builder()
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(2))
            .area("부산광역시")
            .title("부산 여행")
            .build();
        planRepository.save(plan1);

        Plan plan2 = Plan.builder()
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(2))
            .area("서울특별시")
            .title("서울 여행")
            .build();
        planRepository.save(plan2);
    }

    @Test
    @DisplayName("여행 코스 목록 조회 - 지역 = 서울특별시")
    void testGetPlans1() {
        //given
        String area = "서울특별시";

        BooleanExpression condition = area != null ? qPlan.area.eq(Area.fromName(area)) : null;
        //when
        List<Plan> result = queryFactory.selectFrom(qPlan)
            .where(condition)
            .fetch();
        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTitle()).isEqualTo("서울 여행");
     }

    @Test
    @DisplayName("여행 코스 목록 조회 - 지역 = null")
    void testGetPlans2() {
        //given
        String area = null;

        BooleanExpression condition = area != null ? qPlan.area.eq(Area.fromName(area)) : null;
        //when
        List<Plan> result = queryFactory.selectFrom(qPlan)
            .where(condition)
            .fetch();
        //then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("여행 코스 목록 조회 - 지역 = 경기도")
    void testGetPlans3() {
        //given
        String area = "경기도";

        BooleanExpression condition = area != null ? qPlan.area.eq(Area.fromName(area)) : null;
        //when
        List<Plan> result = queryFactory.selectFrom(qPlan)
            .where(condition)
            .fetch();
        //then
        assertThat(result.size()).isEqualTo(0);
    }

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
            .build();
        plan.addPlanItems(planItems);

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
