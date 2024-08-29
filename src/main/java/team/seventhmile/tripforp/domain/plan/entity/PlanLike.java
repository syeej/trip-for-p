package team.seventhmile.tripforp.domain.plan.entity;

import jakarta.persistence.*;
import lombok.*;
import team.seventhmile.tripforp.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "plan_like")
public class PlanLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // 좋아요 엔티티의 고유 ID

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user; // 좋아요한 사용자에 대한 외래 키

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plan_id", nullable = false)
  private Plan plan; // 좋아요된 여행 코스에 대한 외래 키

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt; // 좋아요가 생성된 시간

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
