package team.seventhmile.tripforp.domain.review_post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.file.entity.MagazineFile;
import team.seventhmile.tripforp.domain.file.entity.ReviewFile;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.review_comment.entity.ReviewComment;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;

import java.util.List;
import team.seventhmile.tripforp.domain.user.entity.User;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReviewPostDto {

	private Long id;

	private Long planId;

	private Long userId;

	@NotBlank
	@Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
	private String title;

	@NotBlank
	@Size(max = 999999, message = "내용은 999999자를 초과할 수 없습니다.")
	private String content;

	private Integer views;

	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;

	private List<String> fileUrls;

	private List<ReviewComment> comments;

	/**
	 * DTO를 ReviewPost 엔티티로 변환합니다.
	 *
	 * @param user 변환할 사용자 엔티티
	 * @param plan 변환할 플랜 엔티티
	 * @return 변환된 ReviewPost 엔티티
	 */
	public ReviewPost convertToEntity(User user, Plan plan) {
		return ReviewPost.builder()
			.id(this.id)
			.plan(plan)
			.user(user)
			.title(this.title)
			.content(this.content)
			.views(0) // 게시글 작성 시 조회수는 항상 0으로 초기화
			.build();
	}

	/**
	 * ReviewPost 엔티티를 DTO로 변환합니다.
	 *
	 * @param reviewPost 변환할 ReviewPost 엔티티
	 * @return 변환된 ReviewPostDto
	 */
	public static ReviewPostDto convertToDto(ReviewPost reviewPost) {
		return ReviewPostDto.builder()
			.id(reviewPost.getId())
			.planId(reviewPost.getPlan().getId())
			.userId(reviewPost.getUser().getId())
			.title(reviewPost.getTitle())
			.content(reviewPost.getContent())
			.views(reviewPost.getViews())
			.createdAt(reviewPost.getCreatedAt())
			.updatedAt(reviewPost.getUpdatedAt())
			.fileUrls(reviewPost.getFiles().stream().map(ReviewFile::getUrl).toList())
			.build();
	}
}
