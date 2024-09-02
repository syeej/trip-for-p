package team.seventhmile.tripforp.domain.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.file.entity.File;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDto {

	private Long id;
	private String originalFileName;
	private String uniqueFileName;
	private String fileType;
	private String filePath;

	public static FileDto fromEntity(File file) {
		return FileDto.builder()
			.id(file.getId())
			.originalFileName(file.getOriginalFileName())
			.uniqueFileName(file.getUniqueFileName())
			.fileType(file.getFileType())
			.filePath(file.getFilePath())
			.build();
	}
}
