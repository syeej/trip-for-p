package team.seventhmile.tripforp.domain.plan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.PlanLike;
import team.seventhmile.tripforp.domain.plan.repository.PlanLikeRepository;
import team.seventhmile.tripforp.domain.user.entity.User;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("여행 코스 좋아요 테스트")
public class PlanLikeServiceTest {

    private PlanLikeService planLikeService;
    private PlanLikeRepository planLikeRepository;

    @BeforeEach
    public void setUp(){
        planLikeRepository = Mockito.mock(PlanLikeRepository.class);
        planLikeService = new PlanLikeService(planLikeRepository);
    }

    @Test
    @DisplayName("좋아요")
    public void likePlan_CreateLike() {
        // 가짜 User와 Plan 객체 생성 (실제 객체가 아닌 모의 객체를 사용)
        User mockUser = Mockito.mock(User.class);
        Plan mockPlan = Mockito.mock(Plan.class);

        // PlanLike 엔티티를 리포지토리에 저장할 때 리턴될 값을 설정
        PlanLike mockPlanLike = new PlanLike();
        when(planLikeRepository.save(any(PlanLike.class))).thenReturn(mockPlanLike);

        // 서비스 메서드 호출
        PlanLike result = planLikeService.likePlan(mockUser, mockPlan);

        // 결과가 null이 아닌지 확인하여, 서비스 로직이 제대로 동작했는지 검증
        assertNotNull(result);
        // 레포지토리의 save 메서드가 호출되었는지 확인
        verify(planLikeRepository).save(any(PlanLike.class));
    }

    @Test
    @DisplayName("좋아요 취소")
    public void unlikePlan_DeleteLike() {
        // 삭제할 PlanLike의 ID 설정
        Long likeId = 1L;

        // 서비스 메서드 호출
        planLikeService.unlikePlan(likeId);

        // 레포지토리의 deleteById 메서드가 해당 ID로 호출되었는지 검증
        verify(planLikeRepository).deleteById(likeId);
    }
}