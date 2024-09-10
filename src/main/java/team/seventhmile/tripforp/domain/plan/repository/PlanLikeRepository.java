package team.seventhmile.tripforp.domain.plan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.PlanLike;

@Repository
public interface PlanLikeRepository extends JpaRepository<PlanLike, Long> {

    PlanLike findByUserIdAndPlanId(Long userId, Long planId);

    int countByPlanId(Long planId); // 좋아요 개수 계산 메서드

    // [마이페이지]내가 좋아요한 여행코스 게시글 목록 조회
    @Query("SELECT pl.plan FROM PlanLike pl WHERE pl.user.email = :email")
    Page<Plan> findPlansByUserEmail(@Param("email") String email, Pageable pageable);

    Boolean existsPlanLikeByUserIdAndPlanId(Long userId, Long planId);
}
