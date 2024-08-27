package team.seventhmile.tripforp.domain.magazine.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.magazine.dto.MagazineDto;
import team.seventhmile.tripforp.domain.magazine.entity.Magazine;
import team.seventhmile.tripforp.domain.magazine.repository.MagazineRepository;

@org.springframework.stereotype.Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MagazineService {

	private final MagazineRepository magazineRepository;

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
	// (AuthCheck 권한확인 아직 미기재)
	@Transactional
	public void createMagazinePost(MagazineDto magazineDto) {
		// Not Null 예외 처리
		Magazine.validateField(magazineDto.getTitle(), "Title");
		Magazine.validateField(magazineDto.getContent(), "Content");

		// 현재 로그인된 사용자를 가져오는 로직 미기재

		// 저장 로직
		magazineRepository.save(MagazineDto.convertToEntity(magazineDto));
	}

	// 매거진 글 수정하기
	// (AuthCheck 권한확인 아직 미기재)
	@Transactional
	public void updateMagazinePost(Long id, MagazineDto magazineDto) {
		// Not Null 예외 처리
		Magazine.validateField(magazineDto.getTitle(), "Title");
		Magazine.validateField(magazineDto.getContent(), "Content");

		Magazine magazine = magazineRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("id: " + id + " not found"));

		// 수정 로직
		magazine.update(magazineDto);
	}

	// 매거진 글 삭제하기 (하드 딜리트)
	// (AuthCheck 권한확인 아직 미기재)
	@Transactional
	public void deleteMagazinePost(Long id) {
		Magazine magazine = magazineRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("id: " + id + " not found"));

		// db 에서 삭제
		magazineRepository.delete(magazine);
	}
}
