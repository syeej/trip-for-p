package team.seventhmile.tripforp.domain.plan.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team.seventhmile.tripforp.domain.plan.dto.GetPopularPlanResponse;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.user.entity.User;

public interface PlanRepository extends JpaRepository<Plan, Long>, PlanRepositoryCustom {
    List<Plan> findByArea(Area area);

    @EntityGraph(attributePaths = {"planItems.place", "planItems", "planLikes"})
    Optional<Plan> findById(Long id);

    // 사용자별 Plan 가져오기
    Optional<Plan> findByIdAndUser(Long id, User user);

    @Query("SELECT new team.seventhmile.tripforp.domain.plan.dto.GetPopularPlanResponse(" +
            "p.id, p.title, SIZE(p.planLikes), " +
            "(SELECT pl.imageUrl FROM PlanItem pi " +
            "JOIN pi.place pl " +
            "WHERE pi.plan.id = p.id AND pl.imageUrl != '' " +
            "ORDER BY pi.tripDate ASC, pi.sequence ASC " +
            "LIMIT 1)) " +
            "FROM Plan p " +
            "WHERE EXISTS (SELECT 1 FROM PlanItem pi " +
            "JOIN pi.place pl " +
            "WHERE pi.plan.id = p.id AND pl.imageUrl != '')" +
            "ORDER BY SIZE(p.planLikes) DESC ")
    List<GetPopularPlanResponse> findPopularPlans(Pageable pageable);
}
