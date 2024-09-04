package team.seventhmile.tripforp.domain.plan.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.plan.entity.Plan;

@NoArgsConstructor
@Getter
public class GetPlanResponse {

    private Long id;
    private String writer;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String area;
    private int views;
    private int likeCount;
    private List<PlanGetItemDto> planItems;

    @QueryProjection
    public GetPlanResponse(Plan plan, int likeCount) {
        this.id = plan.getId();
        this.writer = plan.getUser().getNickname();
        this.title = plan.getTitle();
        this.startDate = plan.getStartDate();
        this.endDate = plan.getEndDate();
        this.area = plan.getArea().getName();
        this.views = plan.getViews();
        this.likeCount = likeCount;
        this.planItems = plan.getPlanItems().stream()
            .map(PlanGetItemDto::new)
            .toList();
    }
}
