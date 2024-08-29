package team.seventhmile.tripforp.domain.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.plan.entity.PlanItem;

public interface PlanItemRepository extends JpaRepository<PlanItem, Long> {

}
