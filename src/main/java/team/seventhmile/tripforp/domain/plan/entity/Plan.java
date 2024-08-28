package team.seventhmile.tripforp.domain.plan.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

import lombok.*;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long id;

    //todo 회원 ID

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Area area;

    //todo PlanItem List

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<PlanItem> planItems;  // PlanItems associated with this Plan

    @Column(nullable = false)
    private int views;

    /**
     * 생성 메서드
     */
    @Builder
    public Plan(LocalDate startDate, LocalDate endDate, String title, Area area) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.area = area;
        this.views = 0;
    }
}
