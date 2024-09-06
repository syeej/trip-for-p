package team.seventhmile.tripforp.domain.free_post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.free_comment.dto.FreeCommentDto;
import team.seventhmile.tripforp.domain.free_comment.dto.GetFreeCommentDto;
import team.seventhmile.tripforp.domain.free_comment.entity.FreeComment;
import team.seventhmile.tripforp.domain.free_post.entity.FreePost;
import team.seventhmile.tripforp.domain.user.entity.User;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreePostDto {

	private Long id;

	private Long userId;

	@NotBlank
	@Size(max = 999999, message = "내용은 999999자를 초과할 수 없습니다.")
	private String content;

	private Integer views;

	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;

	private List<GetFreeCommentDto> comments = new ArrayList<>();

	// DTO -> Entity
	public FreePost convertToEntity(User user) {
		return FreePost.builder()
			.id(this.id)
			.user(user)
			.content(this.content)
			.views(0)
			.build();
	}

	// Entity -> DTO
	public static FreePostDto convertToDto(FreePost freePost) {
		return FreePostDto.builder()
			.id(freePost.getId())
			.userId(freePost.getUser().getId())
			.content(freePost.getContent())
			.views(freePost.getViews())
			.createdAt(freePost.getCreatedAt())
			.updatedAt(freePost.getUpdatedAt())
			.comments(freePost.getComments() != null ? freePost.getComments().stream().map(GetFreeCommentDto::new).toList() : new ArrayList<>())
			.build();
	}

}
