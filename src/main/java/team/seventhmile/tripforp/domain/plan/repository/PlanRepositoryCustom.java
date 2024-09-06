package team.seventhmile.tripforp.domain.plan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.seventhmile.tripforp.domain.plan.dto.GetPlanListResponse;
import team.seventhmile.tripforp.domain.plan.entity.Plan;

public interface PlanRepositoryCustom {

    Page<GetPlanListResponse> getPlans(String area, Pageable pageable);

    Page<GetPlanListResponse> getMyPlans(String email, Pageable pageable);

    Plan findPlan(Long id);

}
