package team.seventhmile.tripforp.domain.magazine.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.seventhmile.tripforp.domain.file.entity.File;
import team.seventhmile.tripforp.domain.file.service.FileService;
import team.seventhmile.tripforp.domain.magazine.dto.MagazineDto;
import team.seventhmile.tripforp.domain.magazine.entity.Magazine;
import team.seventhmile.tripforp.domain.magazine.repository.MagazineRepository;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MagazineService {

	private final MagazineRepository magazineRepository;
	private final UserRepository userRepository;
	private final FileService fileService;

	// 매거진 글 목록 조회
	public Optional<List<MagazineDto>> getAllMagazineList() {
		List<MagazineDto> magazines = magazineRepository.findAllByOrderByIdDesc().stream()
			.map(MagazineDto::convertToDto)
			.collect(Collectors.toList());
		return magazines.isEmpty() ? Optional.empty() : Optional.of(magazines);
	}

	// 매거진 글 상세 보기
	public Optional<MagazineDto> getMagazineDetail(Long id) { // 조회하고자 하는 게시글의 고유 id
		return magazineRepository.findById(id)
			.map(MagazineDto::convertToDto); // 조회된 매거진 엔티티가 있다면, 이를 dto 객체로 변환
	}

	// 매거진 글 작성하기
	@Transactional
	public void createMagazinePost(MagazineDto magazineDto, Long userId,
		List<MultipartFile> files) {
		// 현재 로그인된 사용자를 가져오는 로직
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("User not found"));

		if (user.getRole() != Role.ADMIN) {
			throw new IllegalArgumentException("Only ADMIN has the permission");
		}

		// Not Null 예외 처리
		Magazine.validateField(magazineDto.getTitle(), "Title");
		Magazine.validateField(magazineDto.getContent(), "Content");

		// 파일 저장 로직
		List<File> savedFiles = (files != null && !files.isEmpty() ? fileService.saveFiles(files)
			: Collections.emptyList());

		// 매거진 글 생성 및 저장 로직
		Magazine magazine = MagazineDto.convertToEntity(magazineDto, user);
		magazine.setFiles(savedFiles);

		// 저장
		magazineRepository.save(magazine);
	}

	// 매거진 글 수정하기
	@Transactional
	public void updateMagazinePost(Long id, MagazineDto magazineDto, Long userId,
		List<MultipartFile> files) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("User not found"));

		if (user.getRole() != Role.ADMIN) {
			throw new IllegalArgumentException("Only ADMIN has the permission");
		}

		// 예외 처리
		Magazine.validateField(magazineDto.getTitle(), "Title");
		Magazine.validateField(magazineDto.getContent(), "Content");

		Magazine magazine = magazineRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Magazine not found"));

		// 수정 로직
		magazine.update(magazineDto);

		if (files != null && !files.isEmpty()) {
			for (File file : magazine.getFiles()) {
				fileService.deleteFile(file.getId());
			}
			List<File> savedFiles = fileService.saveFiles(files);
			magazine.setFiles(savedFiles);
		}
	}

	// 매거진 글 삭제하기 (하드 딜리트)
	@Transactional
	public void deleteMagazinePost(Long id, Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("User not found"));

		if (user.getRole() != Role.ADMIN) {
			throw new IllegalArgumentException("Only ADMIN has the permission");
		}

		Magazine magazine = magazineRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Magazine not found"));

		if (magazine.getFiles() != null && !magazine.getFiles().isEmpty()) {
			for (File file : magazine.getFiles()) {
				fileService.deleteFile(file.getId());
			}
		}
		// db 에서 삭제
		magazineRepository.delete(magazine);
	}
}
