package team.seventhmile.tripforp.domain.plan.dto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.PlanItem;
import team.seventhmile.tripforp.domain.user.entity.User;

@NoArgsConstructor
@Getter
public class UpdatePlanRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private String title;
    private Area area;
    private List<PlanItem> planItems;

    @Builder
    public UpdatePlanRequest(LocalDate startDate, LocalDate endDate, String title, Area area,
        List<PlanItem> planItems) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.area = area;
        this.planItems = planItems;
    }
}
