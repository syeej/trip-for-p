package team.seventhmile.tripforp.domain.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.user.entity.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanLikeDto {
    private Long id; // 좋아요의 ID
    private User userId; // 좋아요한 사용자의 ID
    private Plan planId; // 좋아요된 여행 코스의 ID
}
