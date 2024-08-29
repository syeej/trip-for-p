package team.seventhmile.tripforp.domain.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.seventhmile.tripforp.domain.plan.entity.Area;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlanGetDetailDto {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Area area;
    private int views;
    private List<PlanItemDto> planItems;
    private List<PlanLikeDto> planLikes;
}
