package team.seventhmile.tripforp.domain.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.plan.entity.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long>, PlanRepositoryCustom {

}
