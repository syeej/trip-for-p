package team.seventhmile.tripforp.domain.magazine.controller;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.domain.magazine.dto.MagazineDto;
import team.seventhmile.tripforp.domain.magazine.service.MagazineService;

@RestController
@RequestMapping("/api/magazines")
public class MagazineController {

	private final MagazineService magazineService;

	@Autowired
	public MagazineController(MagazineService magazineService) {
		this.magazineService = magazineService;
	}

	// 매거진 글 목록 조회
	@GetMapping
	public ResponseEntity<List<MagazineDto>> getAllMagazineList() {
		List<MagazineDto> magazine = magazineService.getAllMagazineList()
			.orElse(Collections.emptyList());
		return ResponseEntity.ok(magazine);
	}

	// 매거진 글 상세 보기
	@GetMapping("/{id}")
	public ResponseEntity<MagazineDto> getMagazineDetail(@PathVariable("id") Long id) {
		return magazineService.getMagazineDetail(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	// 매거진 글 작성 하기
	@PostMapping
	public ResponseEntity<MagazineDto> createMagazinePost(
		@Valid @RequestBody MagazineDto magazineDto) {
		magazineService.createMagazinePost(magazineDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(magazineDto);
	}

	// 매거진 글 수정 하기
	@PutMapping("/{id}")
	public ResponseEntity<MagazineDto> updateMagazinePost(
		@PathVariable("id") Long id,
		@Valid @RequestBody MagazineDto magazineDto) {
		magazineService.updateMagazinePost(id, magazineDto);
		return ResponseEntity.ok(magazineDto);
	}

	// 매거진 글 삭제 하기
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMagazinePost(@PathVariable("id") Long id) {
		magazineService.deleteMagazinePost(id);
		return ResponseEntity.noContent().build();
	}
}
