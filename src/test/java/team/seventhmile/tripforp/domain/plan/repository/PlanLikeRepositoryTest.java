package team.seventhmile.tripforp.domain.plan.repository;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import team.seventhmile.tripforp.domain.plan.entity.PlanLike;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PlanLikeRepositoryTest {

    @Test
    public void save_PersistPlanLike() {
        // 가짜 PlanLikeRepository 생성
        PlanLikeRepository planLikeRepository = Mockito.mock(PlanLikeRepository.class);

        // 테스트용 PlanLike 엔티티 생성
        PlanLike planLike = new PlanLike();

        // 모킹된 리포지토리에서 save 메서드를 호출할 때 반환할 값을 설정
        Mockito.when(planLikeRepository.save(planLike)).thenReturn(planLike);

        // PlanLike 객체를 리포지토리에 저장
        PlanLike savedPlanLike = planLikeRepository.save(planLike);

        // 저장된 객체가 null이 아닌지 확인
        assertNotNull(savedPlanLike);
    }
}

