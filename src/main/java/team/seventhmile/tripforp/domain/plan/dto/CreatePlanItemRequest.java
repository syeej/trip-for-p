package team.seventhmile.tripforp.domain.plan.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.plan.entity.Place;

@NoArgsConstructor
@Getter
public class CreatePlanItemRequest {

    private CreatePlaceRequest place;
    private int sequence;
    private LocalDate tripDate;
    private String memo;

    @Builder
    public CreatePlanItemRequest(CreatePlaceRequest place, int sequence, LocalDate tripDate, String memo) {
        this.place = place;
        this.sequence = sequence;
        this.tripDate = tripDate;
        this.memo = memo;
    }
}
