package team.seventhmile.tripforp.magazine;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
public class Dto {

	private Long id;

	@Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
	private String title;

	@Size(max = 999999, message = "내용은 999999자를 초과할 수 없습니다.")
	private String content;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	// dto -> entity 변환
	public static Entity convertToEntity(Dto dto) {
		return Entity.builder()
			.title(dto.getTitle())
			.content(dto.getContent())
			.createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now())
			.updatedAt(dto.getUpdatedAt())
			.build();
	}

	// entity -> dto 변환
	public static Dto convertToDto(Entity entity) {
		return Dto.builder()
			.id(entity.getId())
			.title(entity.getTitle())
			.content(entity.getContent())
			.createdAt(entity.getCreatedAt())
			.updatedAt(entity.getUpdatedAt())
			.build();
	}
}
