package team.seventhmile.tripforp.domain.plan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.plan.dto.PlanLikeRequestDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanLikeResponseDto;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.PlanLike;
import team.seventhmile.tripforp.domain.plan.repository.PlanLikeRepository;
import team.seventhmile.tripforp.domain.plan.repository.PlanRepository;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class PlanLikeService {

  private final PlanLikeRepository planLikeRepository;
  private final UserRepository userRepository;
  private final PlanRepository planRepository;

  // 특정 사용자가 특정 여행 코스를 좋아요하는 로직 처리
  @Transactional
  public PlanLikeResponseDto likePlan(PlanLikeRequestDto requestDto) {

    User user =
        userRepository
            .findById(requestDto.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException(User.class, requestDto.getUserId()));

    Plan plan =
        planRepository
            .findById(requestDto.getPlanId())
            .orElseThrow(() -> new ResourceNotFoundException(Plan.class, requestDto.getPlanId()));

    // PlanLike 객체 생성 및 저장
    PlanLike planLike = PlanLike.builder().user(user).plan(plan).build();

    // 저장된 엔티티를 가져옴
    PlanLike savedPlanLike = planLikeRepository.save(planLike);

    // PlanLikeResponseDto 생성 후 반환
    return new PlanLikeResponseDto(
        savedPlanLike.getId(), savedPlanLike.getUser().getId(), savedPlanLike.getPlan().getId());
  }

  // 특정 좋아요 삭제하는 로직 처리
  @Transactional
  public void unlikePlan(Long likeId) {
    planLikeRepository.deleteById(likeId);
  }
}
