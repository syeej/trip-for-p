package team.seventhmile.tripforp.domain.free_post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import team.seventhmile.tripforp.domain.free_post.dto.FreePostDto;
import team.seventhmile.tripforp.domain.free_post.service.FreePostService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/free-posts")
public class FreePostController {

	private final FreePostService freePostService;

	/**
	 * 새로운 자유 게시글을 작성합니다.
	 *
	 * @param user 현재 인증된 사용자 정보
	 * @param freePostDto 작성할 자유 게시글 정보
	 * @return 작성된 자유 게시글의 DTO
	 */
	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public FreePostDto createFreePost(
		@AuthenticationPrincipal UserDetails user,
		@RequestBody FreePostDto freePostDto) {
		return freePostService.createFreePost(freePostDto, user.getUsername());
	}

	/**
	 * 기존 자유 게시글을 수정합니다.
	 *
	 * @param id 수정할 자유 게시글의 ID
	 * @param freePostDto 수정할 자유 게시글 정보
	 * @param user 현재 인증된 사용자 정보
	 * @return 수정된 자유 게시글의 DTO
	 */
	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{id}")
	public FreePostDto updateFreePost(@PathVariable("id") Long id,
		@RequestBody FreePostDto freePostDto,
		@AuthenticationPrincipal UserDetails user) {
		return freePostService.updateFreePost(id, freePostDto, user.getUsername());
	}


	/**
	 * 자유 게시글을 삭제합니다.
	 *
	 * @param id 삭제할 자유 게시글의 ID
	 * @param user 현재 인증된 사용자 정보
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@DeleteMapping("/{id}")
	public void deleteFreePost(@PathVariable("id") Long id,
		@AuthenticationPrincipal UserDetails user) {
		freePostService.deleteFreePost(id, user.getUsername());
	}

	/**
	 * 자유 게시글 목록을 페이지네이션하여 조회합니다.
	 *
	 * @param page 페이지 번호 (기본값 0)
	 * @param size 페이지 크기 (기본값 10)
	 * @param keyword 검색 키워드 (선택사항)
	 * @return 페이지네이션된 자유 게시글 목록
	 */
	@GetMapping
	public Page<FreePostDto> getFreePosts(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "keyword", required = false) String keyword) {
		PageRequest pageRequest = PageRequest.of(page, size);
		if (keyword != null && !keyword.trim().isEmpty()) {
			return freePostService.getFreePostSearch(keyword, pageRequest);
		} else {
			return freePostService.getAllFreePost(pageRequest);
		}
	}

	/**
	 * 특정 자유 게시글을 상세 조회합니다.
	 *
	 * @param id 조회할 자유 게시글의 ID
	 * @return 조회된 자유 게시글의 DTO
	 */
	@GetMapping("/{id}")
	public FreePostDto getFreePostDetail(@PathVariable("id") Long id) {
		return freePostService.getFreePostDetail(id);
	}
}

