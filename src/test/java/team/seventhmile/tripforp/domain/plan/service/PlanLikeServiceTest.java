package team.seventhmile.tripforp.domain.plan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import team.seventhmile.tripforp.domain.plan.dto.PlanLikeRequestDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanLikeResponseDto;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.PlanLike;
import team.seventhmile.tripforp.domain.plan.repository.PlanLikeRepository;
import team.seventhmile.tripforp.domain.plan.repository.PlanRepository;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("여행 코스 좋아요 서비스 테스트")
public class PlanLikeServiceTest {

  private PlanLikeService planLikeService;
  private PlanLikeRepository planLikeRepository;
  private UserRepository userRepository;
  private PlanRepository planRepository;

  @BeforeEach
  public void setUp() {
    planLikeRepository = Mockito.mock(PlanLikeRepository.class);
    userRepository = Mockito.mock(UserRepository.class);
    planRepository = Mockito.mock(PlanRepository.class);

    planLikeService = new PlanLikeService(planLikeRepository, userRepository, planRepository);
  }

  @Test
  @DisplayName("사용자와 플랜을 받아 좋아요 생성 테스트")
  public void likePlan_CreateLike() {
    // 가짜 User와 Plan 생성
    User mockUser = Mockito.mock(User.class);
    Plan mockPlan = Mockito.mock(Plan.class);

    // ID 값을 모킹하여 설정
    Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
    Mockito.when(planRepository.findById(1L)).thenReturn(java.util.Optional.of(mockPlan));

    // PlanLike 엔티티를 리포지토리에 저장할 때 리턴될 값을 설정
    PlanLike mockPlanLike = PlanLike.builder()
            .id(1L)  // 이 부분에서 ID를 수동으로 설정합니다.
            .user(mockUser)
            .plan(mockPlan)
            .build();

    when(planLikeRepository.save(any(PlanLike.class))).thenReturn(mockPlanLike);

    // DTO 생성
    PlanLikeRequestDto requestDto = new PlanLikeRequestDto(1L, 1L);

    // 서비스 메서드 호출
    PlanLikeResponseDto result = planLikeService.likePlan(requestDto);

    // 결과가 null이 아닌지 확인하여, 서비스 로직이 제대로 동작했는지 검증
    assertNotNull(result);  // result 자체가 null이 아님을 확인
    assertNotNull(result.getId());  // result의 id가 null이 아님을 확인
    assertNotNull(result.getUserId());  // result의 userId가 null이 아님을 확인
    assertNotNull(result.getPlanId());  // result의 planId가 null이 아님을 확인

    // 레포지토리의 save 메서드가 호출되었는지 확인
    verify(planLikeRepository).save(any(PlanLike.class));
  }

  @Test
  @DisplayName("좋아요 취소시 레포지토리에서 잘 삭제되는지 검증")
  public void unlikePlan_DeleteLike() {
    // 삭제할 PlanLike의 ID 설정
    Long likeId = 1L;

    // 서비스 메서드 호출
    planLikeService.unlikePlan(likeId);

    // 레포지토리의 deleteById 메서드가 해당 ID로 호출되었는지 검증
    verify(planLikeRepository).deleteById(likeId);
  }
}