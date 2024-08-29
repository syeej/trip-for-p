package team.seventhmile.tripforp.domain.plan.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.plan.entity.Plan;

import java.util.Optional;

public interface PlanGetDetailRepository extends JpaRepository<Plan, Long> {
    @EntityGraph(attributePaths = {"planItems.place", "planItems", "planLikes"})
    Optional<Plan> findById(Long id);
}
