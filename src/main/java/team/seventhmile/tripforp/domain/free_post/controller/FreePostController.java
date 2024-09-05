package team.seventhmile.tripforp.domain.free_post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

	// 자유 게시글 생성
	@PostMapping
	public FreePostDto createFreePost(
		@AuthenticationPrincipal UserDetails user,
		@RequestPart(value = "request") FreePostDto freePostDto) {
		return freePostService.createFreePost(freePostDto, user.getUsername());
	}

	// 자유 게시글 수정
	@PutMapping("/{id}")
	public FreePostDto updateFreePost(@PathVariable("id") Long id,
		@RequestBody FreePostDto freePostDto,
		@AuthenticationPrincipal UserDetails user) {
		return freePostService.updateFreePost(id, freePostDto, user.getUsername());
	}

	// 자유 게시글 삭제
	@DeleteMapping("/{id}")
	public void deleteFreePost(@PathVariable("id") Long id,
		@AuthenticationPrincipal UserDetails user) {
		freePostService.deleteFreePost(id, user.getUsername());
	}

	// 자유 게시글 목록 조회
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

	// 자유 게시글 상세 조회
	@GetMapping("/{id}")
	public FreePostDto getFreePostDetail(@PathVariable("id") Long id) {
		return freePostService.getFreePostDetail(id);
	}
}

