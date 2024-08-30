package team.seventhmile.tripforp.domain.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import team.seventhmile.tripforp.domain.plan.entity.PlanItem;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlanGetItemDto {
    private Long id;
    private PlaceDto place;
    private LocalDate tripDate;
    private String memo;
    private int sequence;

    public PlanGetItemDto(PlanItem planItem) {
        this.id = planItem.getId();
        this.place = new PlaceDto(planItem.getPlace());
        this.tripDate = planItem.getTripDate();
        this.sequence = planItem.getSequence();
        this.memo = planItem.getMemo();
    }
}

