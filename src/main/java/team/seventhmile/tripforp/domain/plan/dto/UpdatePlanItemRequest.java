package team.seventhmile.tripforp.domain.plan.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdatePlanItemRequest {

    private Long id;
    private String action;
    private int sequence;
    private LocalDate tripDate;
    private String memo;
    private CreatePlaceRequest place;
}
