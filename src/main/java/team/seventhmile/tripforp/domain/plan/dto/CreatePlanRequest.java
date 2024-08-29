package team.seventhmile.tripforp.domain.plan.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.PlanItem;

@NoArgsConstructor
@Getter
public class CreatePlanRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private String title;
    private String area;
    private List<CreatePlanItemRequest> planItems;

    @Builder
    public CreatePlanRequest(LocalDate startDate, LocalDate endDate, String title, String area,
        List<CreatePlanItemRequest> planItems) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.area = area;
        this.planItems = planItems;
    }
}
