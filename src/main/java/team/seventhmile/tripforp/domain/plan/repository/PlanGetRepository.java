package team.seventhmile.tripforp.domain.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Plan;

import java.util.List;

@Repository
public interface PlanGetRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByArea(Area area);
}
