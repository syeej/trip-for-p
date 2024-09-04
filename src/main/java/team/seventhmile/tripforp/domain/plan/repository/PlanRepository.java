package team.seventhmile.tripforp.domain.plan.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.user.entity.User;

public interface PlanRepository extends JpaRepository<Plan, Long>, PlanRepositoryCustom {
    List<Plan> findByArea(Area area);

    @EntityGraph(attributePaths = {"planItems.place", "planItems", "planLikes"})
    Optional<Plan> findById(Long id);

    // 사용자별 Plan 가져오기
    Optional<Plan> findByIdAndUser(Long id, User user);
}
