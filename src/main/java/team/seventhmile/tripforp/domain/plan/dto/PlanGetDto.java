package team.seventhmile.tripforp.domain.plan.dto;

import lombok.*;
import team.seventhmile.tripforp.domain.user.entity.User;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlanGetDto {
    private User user;
    private String title;
    private int views;
    private List<PlanListItemDto> planItems;

}