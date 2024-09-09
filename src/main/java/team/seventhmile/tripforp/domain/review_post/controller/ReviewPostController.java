package team.seventhmile.tripforp.domain.review_post.controller;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ReviewPostDto createReviewPost(
		@AuthenticationPrincipal UserDetails user,
		@RequestPart(value = "request") ReviewPostDto reviewPostDto,
		@RequestPart(value = "files", required = false) List<MultipartFile> files
	) throws IOException {
		return reviewPostService.createReviewPost(reviewPostDto, user.getUsername(), files);
	}

	// 리뷰 게시글 수정
	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{id}")
	public ReviewPostDto updateReviewPost(@PathVariable("id") Long id,
		@AuthenticationPrincipal UserDetails user,
		@RequestPart(value = "request") ReviewPostDto reviewPostDto,
		@RequestPart(value = "files", required = false) List<MultipartFile> files
	) throws IOException {
		return reviewPostService.updateReviewPost(id, reviewPostDto, user.getUsername(), files);
	}

	// 리뷰 게시글 삭제
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
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

	//마이페이지 - 사용자가 작성한 리뷰 게시글 목록 조회
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/me")
	public ResponseEntity<Page<ReviewPostDto>> getMyReviewPostList(@AuthenticationPrincipal UserDetails user,
																   Pageable pageable){
		return ResponseEntity.ok(reviewPostService.getMyReviewList(user, pageable));
	}
}
