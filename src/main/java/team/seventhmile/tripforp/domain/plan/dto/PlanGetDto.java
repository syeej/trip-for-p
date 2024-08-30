package team.seventhmile.tripforp.domain.plan.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlanGetDto {
    private String title;
    private int views;
    private List<PlanListItemDto> planItems;

}