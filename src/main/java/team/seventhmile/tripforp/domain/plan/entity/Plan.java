package team.seventhmile.tripforp.domain.plan.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.*;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanRequest;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.global.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "plans")
public class Plan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Area area;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanItem> planItems = new ArrayList<>();  // PlanItems associated with this Plan

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<PlanLike> planLikes;  // PlanItems associated with this Plan
    @Column(nullable = false)
    private int views;

    /**
     * 생성 메서드
     */
    @Builder
    public Plan(User user, LocalDate startDate, LocalDate endDate, String title, String area) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.area = Area.fromName(area);
        this.views = 0;
    }

    /**
     * 수정 메서드
     */
    public void updatePlan(UpdatePlanRequest request) {
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.title = request.getTitle();
        this.area = Area.fromName(request.getArea());
    }

    public void addPlanItem(PlanItem planItem) {
        this.planItems.add(planItem);
        planItem.setPlan(this);
    }

    public void removePlanItem(PlanItem planItem) {
        this.planItems.remove(planItem);
        planItem.setPlan(null);
    }

    public void addPlanItems(List<PlanItem> planItems) {
        for (PlanItem planItem : planItems) {
            addPlanItem(planItem);
        }
    }

    public void removePlanItems(List<PlanItem> planItems) {
        for (PlanItem planItem : planItems) {
            removePlanItem(planItem);
        }
    }

    public void increaseViews() {
        this.views += 1;
    }
}
