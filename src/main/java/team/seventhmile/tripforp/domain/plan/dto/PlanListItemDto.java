package team.seventhmile.tripforp.domain.plan.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlanListItemDto {
    private int sequence;       // PlanItem sequence
    private String placeTitle;  // Place's title from PlanItem
}