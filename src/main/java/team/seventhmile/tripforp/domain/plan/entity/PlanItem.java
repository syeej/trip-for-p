package team.seventhmile.tripforp.domain.plan.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
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
