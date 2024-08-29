package team.seventhmile.tripforp.domain.plan.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "plan_items")
public class PlanItem{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @Column(nullable = false)
    private int sequence;

    @Column(nullable = false)
    private LocalDate tripDate;

    @Column
    private String memo;
    // @Builder를 사용한 커스텀 생성자
    @Builder
    public PlanItem(Plan plan, Place place, int sequence, LocalDate tripDate, String memo) {
        this.plan = plan;
        this.place = place;
        this.sequence = sequence;
        this.tripDate = tripDate;
        this.memo = memo;
    }
}
