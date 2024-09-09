package team.seventhmile.tripforp.domain.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.plan.entity.PlanLike;

@Repository
public interface PlanLikeRepository extends JpaRepository<PlanLike, Long> {

    int countByPlanId(Long planId); // 좋아요 개수 계산 메서드
}
