package team.seventhmile.tripforp.domain.magazine.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import team.seventhmile.tripforp.domain.magazine.dto.MagazineDto;
import team.seventhmile.tripforp.domain.magazine.service.MagazineService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/magazines")
public class MagazineController {

	private final MagazineService magazineService;

	// 매거진 게시글 작성
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public MagazineDto createMagazinePost(
		@AuthenticationPrincipal UserDetails user,
		@RequestPart(value = "request") MagazineDto magazineDto,
		@RequestPart(value = "files", required = false) List<MultipartFile> files) {
		return magazineService.createMagazinePost(magazineDto, user.getUsername(), files);
	}

	// 매거진 게시글 수정
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public MagazineDto updateMagazinePost(@PathVariable("id") Long id,
		@RequestBody MagazineDto magazineDto,
		@AuthenticationPrincipal UserDetails user,
		@RequestPart(value = "files", required = false) List<MultipartFile> files) {
		return magazineService.updateMagazinePost(id, magazineDto, user.getUsername(), files);
	}

	// 매거진 게시글 삭제
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public void deleteMagazinePost(@PathVariable("id") Long id,
		@AuthenticationPrincipal UserDetails user) {
		magazineService.deleteMagazinePost(id, user.getUsername());
	}

	// 매거진 게시글 목록 조회
	@GetMapping
	public ResponseEntity<Page<MagazineDto>> getAllMagazineList(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "keyword", required = false) String keyword) {
		PageRequest pageRequest = PageRequest.of(page, size);
		if (keyword != null && !keyword.trim().isEmpty()) {
			return ResponseEntity.ok(magazineService.getMagazineSearch(keyword, pageRequest));
		} else {
			return ResponseEntity.ok(magazineService.getAllMagazineList(pageRequest));
		}
	}

	// 매거진 게시글 상세 조회
	@GetMapping("/{id}")
	public MagazineDto getMagazineDetail(@PathVariable("id") Long id) {
		return magazineService.getMagazineDetail(id);
	}
}
