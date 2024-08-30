package team.seventhmile.tripforp.domain.review_comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCommentDto {

	private Long id;

	@NotBlank(message = "내용을 입력해주세요.")
	@Size(max = 500, message = "댓글은 500자 이하로 입력해주세요.")
	private String content;

	private Long postId;
	private Long authorId;
}
