package team.seventhmile.tripforp.domain.review_post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.free_comment.entity.FreeComment;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewPostDto {

    private Long id;

    private Long planId;

    private Long userId;

    private String title;

    private String content;

    private Integer views;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    // 이 부분 만약 변경 사항이 있을 시 타입 명만 수정해주면 됩니다.
    private List<FreeComment> comments;

    // TODO private File file;

    // DTO -> Entity
    public ReviewPost toEntity() {
        return ReviewPost.builder()
                .id(this.id)
                //.plan(this.planId)
                //.user(this.userId)
                .title(this.title)
                .content(this.content)
                .views(this.views)
                .build();
    }

    // Entity -> DTO
    public static ReviewPostDto fromEntity(ReviewPost reviewPost) {
        return ReviewPostDto.builder()
                .id(reviewPost.getId())
                .planId(reviewPost.getPlan().getId())
                .userId(reviewPost.getUser().getId())
                .title(reviewPost.getTitle())
                .content(reviewPost.getContent())
                .views(reviewPost.getViews())
                .build();
    }

}
