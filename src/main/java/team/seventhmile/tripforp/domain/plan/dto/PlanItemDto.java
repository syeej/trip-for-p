package team.seventhmile.tripforp.domain.plan.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.plan.entity.PlanItem;

@NoArgsConstructor
@Getter
public class PlanItemDto {

    private PlaceDto place;
    private LocalDate tripDate;
    private int sequence;
    private String memo;

    public PlanItemDto(PlanItem planItem) {
        this.place = new PlaceDto(planItem.getPlace());
        this.tripDate = planItem.getTripDate();
        this.sequence = planItem.getSequence();
        this.memo = planItem.getMemo();
    }
}
