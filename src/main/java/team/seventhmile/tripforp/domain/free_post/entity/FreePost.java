package team.seventhmile.tripforp.domain.free_post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import team.seventhmile.tripforp.domain.free_comment.entity.FreeComment;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.global.common.BaseEntity;

import java.util.List;
import team.seventhmile.tripforp.global.exception.ValidationUtils;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "free_posts")
@Builder
public class FreePost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "free_post_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer views;

    @OneToMany(mappedBy = "freePost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FreeComment> comments;

    /**
     * 자유 게시글의 내용을 수정합니다.
     *
     * @param content 수정할 내용
     */
    public void update(String content) {
        // 필드 유효성 검사
        ValidationUtils.validateField(content, "Content");

        // 내용 업데이트
        this.content = content;
    }

    /**
     * 리뷰 게시글의 조회 수를 1 증가시킵니다.
     */
    public void incrementViews() {
        this.views += 1;
    }
}

