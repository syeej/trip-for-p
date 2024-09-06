package team.seventhmile.tripforp.domain.magazine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.file.entity.MagazineFile;
import team.seventhmile.tripforp.domain.magazine.entity.Magazine;
import team.seventhmile.tripforp.domain.user.entity.User;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MagazineDto {

	private Long id;

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

	/**
	 * DTO를 Magazine 엔티티로 변환합니다.
	 *
	 * @param user 변환할 사용자 엔티티
	 * @return 변환된 Magazine 엔티티
	 */
	public Magazine convertToEntity(User user) {
		return Magazine.builder()
			.id(this.id)
			.user(user)
			.title(this.title)
			.content(this.content)
			.views(this.views != null ? this.views : 0)
			.createdAt(this.createdAt != null ? this.createdAt : ZonedDateTime.now())
			.updatedAt(this.updatedAt)
			.build();
	}

	/**
	 * Magazine 엔티티를 DTO로 변환합니다.
	 *
	 * @param magazine 변환할 Magazine 엔티티
	 * @return 변환된 Magazine DTO
	 */
	public static MagazineDto convertToDto(Magazine magazine) {
		return MagazineDto.builder()
			.id(magazine.getId())
			.title(magazine.getTitle())
			.content(magazine.getContent())
			.views(magazine.getViews())
			.createdAt(magazine.getCreatedAt())
			.updatedAt(magazine.getUpdatedAt())
			.fileUrls(magazine.getFiles().stream().map(MagazineFile::getUrl).toList())
			.build();
	}

}
