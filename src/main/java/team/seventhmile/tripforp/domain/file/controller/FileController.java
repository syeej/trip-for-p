package team.seventhmile.tripforp.domain.file.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import team.seventhmile.tripforp.domain.file.dto.FileDto;
import team.seventhmile.tripforp.domain.file.entity.File;
import team.seventhmile.tripforp.domain.file.service.FileService;

@RestController
@RequestMapping("/api/files")
public class FileController {

	private final FileService fileService;

	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping("/upload")
	public ResponseEntity<List<FileDto>> uploadFiles(
		@RequestParam("files") List<MultipartFile> files) {
		List<File> savedFiles = fileService.saveFiles(files);
		List<FileDto> fileDtos = savedFiles.stream()
			.map(FileDto::fromEntity)
			.collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.CREATED).body(fileDtos);
	}

	@DeleteMapping("/{fileId}")
	public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
		fileService.deleteFile(fileId);
		return ResponseEntity.noContent().build();
	}
}
