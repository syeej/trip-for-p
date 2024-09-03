package team.seventhmile.tripforp.domain.file.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.seventhmile.tripforp.domain.file.entity.File;
import team.seventhmile.tripforp.domain.file.repository.FileRepository;

@Service
public class FileService {

	private static final String UPLOAD_DIR = "uploads/";
	private final FileRepository fileRepository;

	public FileService(FileRepository fileRepository) {
		this.fileRepository = fileRepository;
	}

	public File saveFile(MultipartFile file) {
		validateFile(file);
		String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());
		Path filePath = Paths.get(UPLOAD_DIR + uniqueFileName);

		try {
			Files.createDirectories(filePath.getParent()); // 디렉토리가 없을 경우 생성
			file.transferTo(filePath.toFile());

			File fileEntity = File.builder()
				.originalFileName(file.getOriginalFilename())
				.uniqueFileName(uniqueFileName)
				.fileType(file.getContentType())
				.filePath(filePath.toString())
				.build();

			return fileRepository.save(fileEntity);
		} catch (IOException e) {
			throw new RuntimeException("Failed to save file", e);
		}
	}

	public List<File> saveFiles(List<MultipartFile> files) {
		return files.stream()
			.map(this::saveFile)
			.collect(Collectors.toList());
	}

	public void deleteFile(Long fileId) {
		File file = fileRepository.findById(fileId)
			.orElseThrow(() -> new IllegalArgumentException("File not found"));
		Path filePath = Paths.get(file.getFilePath());

		try {
			Files.deleteIfExists(filePath);
			fileRepository.deleteById(fileId);
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete file", e);
		}
	}

	private void validateFile(MultipartFile file) {
		String extension = getFileExtension(file.getOriginalFilename());
		if (!isAllowedExtension(extension)) {
			throw new IllegalArgumentException("지원하지 않는 파일 형식입니다.");
		}
	}

	private String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	private boolean isAllowedExtension(String extension) {
		List<String> allowedExtensions = List.of("jpeg", "jpg", "png", "gjf", "bmp", "mp4", "mov");
		return allowedExtensions.contains(extension.toLowerCase());
	}

	private String generateUniqueFileName(String originalFileName) {
		String extension = getFileExtension(originalFileName);
		String uniqueName = UUID.randomUUID().toString();
		return uniqueName + "." + extension;
	}
}
