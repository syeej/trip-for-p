package team.seventhmile.tripforp.domain.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlanLikeDto {
    private Long id; // 좋아요의 ID
    private Long user_Id; // 좋아요한 사용자의 ID
    private Long plan_Id; // 좋아요된 여행 코스의 ID
}
