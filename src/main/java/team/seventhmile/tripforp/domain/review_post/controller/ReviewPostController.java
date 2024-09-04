package team.seventhmile.tripforp.domain.review_post.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.seventhmile.tripforp.domain.review_post.dto.ReviewPostDto;
import team.seventhmile.tripforp.domain.review_post.service.ReviewPostService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review-posts")
public class ReviewPostController {

	private final ReviewPostService reviewPostService;

	// 리뷰 게시글 작성
	@PostMapping
	public ReviewPostDto createReviewPost(
		@AuthenticationPrincipal UserDetails user,
		@RequestPart(value = "request") ReviewPostDto reviewPostDto,
		@RequestPart(value = "files", required = false) List<MultipartFile> files) {
		return reviewPostService.createReviewPost(reviewPostDto, user.getUsername(), files);
	}

	// 리뷰 게시글 수정
	@PutMapping("/{id}")
	public ReviewPostDto updateReviewPost(@PathVariable("id") Long id,
		@RequestBody ReviewPostDto reviewPostDto,
		@AuthenticationPrincipal UserDetails user,
		@RequestPart(value = "files", required = false) List<MultipartFile> files) {
		return reviewPostService.updateReviewPost(id, reviewPostDto, user.getUsername(), files);
	}

	// 리뷰 게시글 삭제
	@DeleteMapping("/{id}")
	public void deleteReviewPost(@PathVariable("id") Long id,
		@AuthenticationPrincipal UserDetails user) {
		reviewPostService.deleteReviewPost(id, user.getUsername());
	}

	// 리뷰 게시글 목록 조회
	@GetMapping
	public Page<ReviewPostDto> getReviewPosts(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "keyword", required = false) String keyword) {
		PageRequest pageRequest = PageRequest.of(page, size);
		if (keyword != null && !keyword.trim().isEmpty()) {
			return reviewPostService.getReviewPostSearch(keyword, pageRequest);
		} else {
			return reviewPostService.getAllReviewPost(pageRequest);
		}
	}

	// 리뷰 게시글 상세 조회
	@GetMapping("/{id}")
	public ReviewPostDto getReviewPostDetail(@PathVariable("id") Long id) {
		return reviewPostService.getReviewPostDetail(id);
	}
}
