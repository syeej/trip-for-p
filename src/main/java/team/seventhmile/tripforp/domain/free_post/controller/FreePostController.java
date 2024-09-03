package team.seventhmile.tripforp.domain.free_post.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import team.seventhmile.tripforp.domain.free_post.dto.FreePostDto;
import team.seventhmile.tripforp.domain.free_post.service.FreePostService;

@RestController
@RequestMapping("/api/free-posts")
public class FreePostController {

	private final FreePostService freePostService;

	@Autowired
	public FreePostController(FreePostService freePostService) {
		this.freePostService = freePostService;
	}

	// 자유 게시글 생성
	@PostMapping
	public FreePostDto createFreePost(@RequestBody FreePostDto freePostDto) {
		return freePostService.createFreePost(freePostDto);
	}

	// 자유 게시글 수정
	@PutMapping("/{id}")
	public FreePostDto updateFreePost(@PathVariable("id") Long id,
		@RequestBody FreePostDto freePostDto) {
		return freePostService.updateFreePost(id, freePostDto);
	}

	// 자유 게시글 삭제
	@DeleteMapping("/{id}")
	public void deleteFreePost(@PathVariable("id") Long id) {
		freePostService.deleteFreePost(id);
	}

	// 자유 게시글 목록 조회, 검색 목록 조회 통합(검색 키워드 여부)
	@GetMapping
	public Page<FreePostDto> getFreePosts(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "keyword", required = false) String keyword) {

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<FreePostDto> freePostDto;

		if (keyword != null && !keyword.trim().isEmpty()) {
			freePostDto = freePostService.getFreePostSearch(keyword, pageRequest);
		} else {
			freePostDto = freePostService.getAllFreePost(pageRequest);
		}

		return freePostDto;
	}

	// 자유 게시글 상세 조회
	@GetMapping("/{id}")
	public FreePostDto getReviewPostDetail(@PathVariable("id") Long id) {
		return freePostService.getFreePostDetail(id);
	}

}

