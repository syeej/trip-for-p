package team.seventhmile.tripforp.domain.plan.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CreatePlanResponse {

    private Long id;

    @Builder
    public CreatePlanResponse(Long id) {
        this.id = id;
    }
}
