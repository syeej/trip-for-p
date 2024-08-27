package team.seventhmile.tripforp.domain.plan.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.plan.entity.PlanLike;
import team.seventhmile.tripforp.domain.plan.repository.PlanLikeRepository;

@Service
public interface PlanLikeService {

    private final PlanLikeRepository planLikeRepository;

    public PlanLikeService(PlanLikeRepository planLikeRepository) {
        this.planLikeRepository = planLikeRepository;
    }

    // 특정 사용자가 특정 여행 코스를 좋아요하는 로직 처리
    @Transactional
    public PlanLike likePlan(User user, Plan plan) {
        PlanLike planLike = PlanLike.builder()
                .user(user)
                .plan(plan)
                .build();

        return planLikeRepository.save(planLike);  // PlanLike 엔티티를 저장하고 반환
    }

    // 특정 좋아요 삭제하는 로직 처리
    @Transactional
    public void unlikePlan(Long likeId){
        planLikeRepository.deleteById(likeId);
    }
}
