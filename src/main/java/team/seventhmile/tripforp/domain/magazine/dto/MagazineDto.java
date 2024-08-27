package team.seventhmile.tripforp.domain.magazine.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.magazine.entity.Magazine;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
public class MagazineDto {

	private Long id;

	@Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
	private String title;

	@Size(max = 999999, message = "내용은 999999자를 초과할 수 없습니다.")
	private String content;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	// dto -> entity 변환
	public static Magazine convertToEntity(MagazineDto magazineDto) {
		return Magazine.builder()
			.title(magazineDto.getTitle())
			.content(magazineDto.getContent())
			.createdAt(magazineDto.getCreatedAt() != null ? magazineDto.getCreatedAt() : LocalDateTime.now())
			.updatedAt(magazineDto.getUpdatedAt())
			.build();
	}

	// entity -> dto 변환
	public static MagazineDto convertToDto(Magazine magazine) {
		return MagazineDto.builder()
			.id(magazine.getId())
			.title(magazine.getTitle())
			.content(magazine.getContent())
			.createdAt(magazine.getCreatedAt())
			.updatedAt(magazine.getUpdatedAt())
			.build();
	}
}
