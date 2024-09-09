package team.seventhmile.tripforp.domain.magazine.controller;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private MagazineService magazineService;

	/**
	 * 매거진 게시글을 작성합니다.
	 *
	 * @param user 작성자 정보
	 * @param magazineDto 작성할 게시글 정보 DTO
	 * @param files 첨부 파일 리스트
	 * @return 작성된 게시글 정보 DTO
	 */
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public MagazineDto createMagazinePost(
		@AuthenticationPrincipal UserDetails user,
		@RequestPart(value = "request") MagazineDto magazineDto,
		@RequestPart(value = "files", required = false) List<MultipartFile> files
	) throws IOException {
		return magazineService.createMagazinePost(magazineDto, user.getUsername(), files);
	}

	/**
	 * 매거진 게시글을 수정합니다.
	 *
	 * @param id 수정할 게시글의 ID
	 * @param magazineDto 수정할 게시글 정보 DTO
	 * @param user 작성자 정보
	 * @param files 첨부 파일 리스트
	 * @return 수정된 게시글 정보 DTO
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public MagazineDto updateMagazinePost(
		@PathVariable("id") Long id,
		@AuthenticationPrincipal UserDetails user,
		@RequestPart(value = "request") MagazineDto magazineDto,
		@RequestPart(value = "files", required = false) List<MultipartFile> files
	) throws IOException {
		return magazineService.updateMagazinePost(id, magazineDto, user.getUsername(), files);
	}

	/**
	 * 매거진 게시글을 삭제합니다.
	 *
	 * @param id 삭제할 게시글의 ID
	 * @param user 작성자 정보
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public void deleteMagazinePost(@PathVariable("id") Long id,
		@AuthenticationPrincipal UserDetails user) {
		magazineService.deleteMagazinePost(id, user.getUsername());
	}

	/**
	 * 매거진 게시글 목록을 조회합니다.
	 *
	 * @param page 페이지 번호
	 * @param size 페이지 크기
	 * @param keyword 검색 키워드 (옵션)
	 * @return 페이징된 게시글 정보 DTO
	 */
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

	/**
	 * 매거진 게시글을 상세 조회합니다.
	 *
	 * @param id 조회할 게시글의 ID
	 * @return 상세 조회된 게시글 정보 DTO
	 */
	@GetMapping("/{id}")
	public MagazineDto getMagazineDetail(@PathVariable("id") Long id) {
		return magazineService.getMagazineDetail(id);
	}
}
