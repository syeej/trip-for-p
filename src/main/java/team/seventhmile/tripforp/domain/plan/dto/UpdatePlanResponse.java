package team.seventhmile.tripforp.domain.plan.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpdatePlanResponse {

    private Long id;

    @Builder
    public UpdatePlanResponse(Long id) {
        this.id = id;
    }
}
