package team.seventhmile.tripforp.domain.review_comment.dto;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.review_comment.entity.ReviewComment;

@NoArgsConstructor
@Getter
public class GetReviewCommentDto {

    private Long id;
    private String author;
    private String content;
    private ZonedDateTime createdAt;

    public GetReviewCommentDto(ReviewComment reviewComment) {
        this.id = reviewComment.getId();
        this.author = reviewComment.getAuthor().getNickname();
        this.content = reviewComment.getContent();
        this.createdAt = reviewComment.getCreatedAt();
    }
}
