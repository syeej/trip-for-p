package team.seventhmile.tripforp.domain.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.user.entity.User;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlanLikeRequestDto {
    private User user;
    private Plan plan;
}
