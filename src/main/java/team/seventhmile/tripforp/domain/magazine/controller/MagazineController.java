package team.seventhmile.tripforp.domain.magazine.controller;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.domain.magazine.dto.MagazineDto;
import team.seventhmile.tripforp.domain.magazine.service.MagazineService;
import team.seventhmile.tripforp.domain.user.entity.User;

@RestController
@RequestMapping("/api/magazines")
public class MagazineController {

	private final MagazineService magazineService;

	@Autowired
	public MagazineController(MagazineService magazineService) {
		this.magazineService = magazineService;
	}

	/**
	 * 모든 매거진 글 목록을 조회합니다.
	 * @return 매거진 글 목록
	 */
	@GetMapping
	public ResponseEntity<List<MagazineDto>> getAllMagazineList() {
		List<MagazineDto> magazine = magazineService.getAllMagazineList()
			.orElse(Collections.emptyList());
		return ResponseEntity.ok(magazine);
	}

	/**
	 * 특정 ID의 매거진 글을 조회합니다.
	 * @param id 조회할 매거진 글의 ID
	 * @return 매거진 글 상세 정보
	 */
	@GetMapping("/{id}")
	public ResponseEntity<MagazineDto> getMagazineDetail(@PathVariable("id") Long id) {
		return magazineService.getMagazineDetail(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * 새로운 매거진 글을 작성합니다.
	 * @param magazineDto 작성할 매거진 글 정보
	 * @param userDetails 현재 인증된 사용자 정보
	 * @return 생성된 매거진 글 정보
	 */
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<MagazineDto> createMagazinePost(
		@Valid @RequestBody MagazineDto magazineDto,
		@AuthenticationPrincipal UserDetails userDetails) {
		User user = (User) userDetails;
		magazineService.createMagazinePost(magazineDto, user.getId());
		return ResponseEntity.status(HttpStatus.CREATED).body(magazineDto);
	}

	/**
	 * 특정 ID의 매거진 글을 수정합니다.
	 * @param id 수정할 매거진 글의 ID
	 * @param magazineDto 수정할 매거진 글 정보
	 * @param userDetails 현재 인증된 사용자 정보
	 * @return 수정된 매거진 글 정보
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<MagazineDto> updateMagazinePost(
		@PathVariable("id") Long id,
		@Valid @RequestBody MagazineDto magazineDto,
		@AuthenticationPrincipal UserDetails userDetails) {
		User user = (User) userDetails;
		magazineService.updateMagazinePost(id, magazineDto, user.getId());
		return ResponseEntity.ok(magazineDto);
	}

	/**
	 * 특정 ID의 매거진 글을 삭제합니다.
	 * @param id 삭제할 매거진 글의 ID
	 * @param userDetails 현재 인증된 사용자 정보
	 * @return 삭제 성공 여부
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteMagazinePost(
		@PathVariable("id") Long id,
		@AuthenticationPrincipal UserDetails userDetails) {
		User user = (User) userDetails;
		magazineService.deleteMagazinePost(id, user.getId());
		return ResponseEntity.noContent().build();
	}
}
