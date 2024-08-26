package team.seventhmile.tripforp.domain.plan.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.seventhmile.tripforp.domain.plan.entity.Plan;

public interface PlanRepositoryCustom {

    Page<Plan> getPlans(String area, Pageable pageable);

}
