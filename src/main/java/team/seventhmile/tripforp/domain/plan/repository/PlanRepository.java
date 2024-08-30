package team.seventhmile.tripforp.domain.plan.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long>, PlanRepositoryCustom {
    List<Plan> findByArea(Area area);

    @EntityGraph(attributePaths = {"planItems.place", "planItems", "planLikes"})
    Optional<Plan> findById(Long id);
}
