package team.seventhmile.tripforp.domain.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlanLikeResponseDto {
    private Long id;
    private Long userId;
    private Long planId;
}
