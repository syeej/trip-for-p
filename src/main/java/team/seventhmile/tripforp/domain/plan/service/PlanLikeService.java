package team.seventhmile.tripforp.domain.plan.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.plan.dto.PlanLikeResponseDto;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.PlanLike;
import team.seventhmile.tripforp.domain.plan.repository.PlanLikeRepository;
import team.seventhmile.tripforp.domain.user.entity.User;

import java.util.List;

@Service
public class PlanLikeService {

    private final PlanLikeRepository planLikeRepository;

    public PlanLikeService(PlanLikeRepository planLikeRepository) {
        this.planLikeRepository = planLikeRepository;
    }

    // 특정 사용자가 특정 여행 코스를 좋아요하는 로직 처리
    @Transactional
    public PlanLikeResponseDto likePlan(User user, Plan plan) {
        PlanLike planLike = PlanLike.builder()
                .user(user)
                .plan(plan)
                .build();

        // 저장된 엔티티를 가져옴
        PlanLike savedPlanLike = planLikeRepository.save(planLike);

        // PlanLikeResponseDto 생성 후 반환
        return new PlanLikeResponseDto(savedPlanLike.getId(), savedPlanLike.getUser().getId(), savedPlanLike.getPlan().getId());
    }

    // 특정 좋아요 삭제하는 로직 처리
    @Transactional
    public void unlikePlan(Long likeId){
        planLikeRepository.deleteById(likeId);
    }

    // 좋아요 기준 상위5개 여행코스 반환 메서드
    public List<Plan> getTop5PlansByLikes(){
        return planLikeRepository.findTop5PlansByLikes();
    }
}
