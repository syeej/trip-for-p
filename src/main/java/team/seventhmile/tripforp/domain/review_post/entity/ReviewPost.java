package team.seventhmile.tripforp.domain.review_post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import team.seventhmile.tripforp.domain.plan.entity.Plan;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "review_posts")
public class ReviewPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_post_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    // TODO private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer views;

    @Column(nullable = false)
    private LocalDate createdAt;

    @Column(nullable = false)
    private LocalDate updatedAt;

    // TODO private List<Comments> comments;

    // TODO private File file;

    public void update(Plan plan,
                       String title,
                       String content,
                       Integer views,
                       LocalDate updatedAt) {
        this.plan = plan;
        this.title = title;
        this.content = content;
        this.views = views;
        this.updatedAt = updatedAt;
    }

}
