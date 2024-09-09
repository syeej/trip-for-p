package team.seventhmile.tripforp.domain.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanResponse;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.PlanLike;

import java.util.List;

@Repository
public interface PlanLikeRepository extends JpaRepository<PlanLike, Long> {

    PlanLike findByUserIdAndPlanId(Long userId, Long planId);

    int countByPlanId(Long planId); // 좋아요 개수 계산 메서드

    // 좋아요 기준 상위 5개 여행코스 조회
    @Query("SELECT pl.plan FROM PlanLike pl GROUP BY pl.plan ORDER BY COUNT(pl.id) DESC")
    List<Plan> findTop5PlansByLikes();
}