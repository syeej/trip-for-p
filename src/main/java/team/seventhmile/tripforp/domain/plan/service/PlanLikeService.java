package team.seventhmile.tripforp.domain.plan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.plan.dto.PlanLikeResponseDto;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.PlanLike;
import team.seventhmile.tripforp.domain.plan.repository.PlanLikeRepository;
import team.seventhmile.tripforp.domain.plan.repository.PlanRepository;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanLikeService {

  private final PlanLikeRepository planLikeRepository;
  private final UserRepository userRepository;
  private final PlanRepository planRepository;

  // 좋아요 또는 좋아요 취소 처리
  @Transactional
  public PlanLikeResponseDto toggleLikePlan(String email, Long planId) {
    // 이메일로 사용자 조회
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

    // userId와 planId로 이미 좋아요 했는지 확인
    PlanLike existingLike = planLikeRepository.findByUserIdAndPlanId(user.getId(), planId);

    if (existingLike != null) {
      // 좋아요가 존재하면 삭제
      planLikeRepository.delete(existingLike);
      return new PlanLikeResponseDto(false, "좋아요 취소됨");
    } else {
      // 좋아요가 없으면 추가
      Plan plan = planRepository.findById(planId)
              .orElseThrow(() -> new RuntimeException("플랜을 찾을 수 없습니다."));

      // PlanLike 객체 생성 (id는 자동 생성됨)
      PlanLike newLike = new PlanLike(null, user, plan);
      planLikeRepository.save(newLike);
      return new PlanLikeResponseDto(true, "좋아요 완료");
    }
  }

  // 좋아요 기준 상위5개 여행코스 반환 메서드
  public List<Plan> getTop5PlansByLikes() {
    return planLikeRepository.findTop5PlansByLikes();
  }
}
