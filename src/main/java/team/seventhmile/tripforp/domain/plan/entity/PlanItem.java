package team.seventhmile.tripforp.domain.plan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanItemRequest;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "plan_items",
    uniqueConstraints = @UniqueConstraint(columnNames = {"plan_id", "sequence", "trip_date"}))
public class PlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(nullable = false)
    private int sequence;

    @Column(nullable = false)
    private LocalDate tripDate;

    @Column
    private String memo;

    @Builder
    public PlanItem(Place place, int sequence, LocalDate tripDate, String memo) {
        this.place = place;
        this.sequence = sequence;
        this.tripDate = tripDate;
        this.memo = memo;
    }

    public Long updatePlanItem(Place place, UpdatePlanItemRequest request) {
        this.place = place;
        this.sequence = request.getSequence();
        this.tripDate = request.getTripDate();
        this.memo = request.getMemo();

        return id;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }
}
