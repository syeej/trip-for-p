package team.seventhmile.tripforp.domain.plan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
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
}
