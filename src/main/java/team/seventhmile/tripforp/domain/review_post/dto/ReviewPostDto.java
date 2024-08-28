package team.seventhmile.tripforp.domain.review_post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewPostDto {

    private Long id;

    private Long planId;

    // TODO private Long userId;

    private String title;

    private String content;

    private Integer views;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    // TODO private List<Long> commentsId;

    // TODO private File file;

    // DTO -> Entity
    public ReviewPost toEntity() {
        return ReviewPost.builder()
                .id(this.id)
                //.plan(this.planId)
                .title(this.title)
                .content(this.content)
                .views(this.views)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    // Entity -> DTO
    public static ReviewPostDto fromEntity(ReviewPost reviewPost) {
        return ReviewPostDto.builder()
                .id(reviewPost.getId())
                .planId(1L) // 임시
                .title(reviewPost.getTitle())
                .content(reviewPost.getContent())
                .views(reviewPost.getViews())
                .createdAt(reviewPost.getCreatedAt())
                .updatedAt(reviewPost.getUpdatedAt())
                .build();
    }

}
