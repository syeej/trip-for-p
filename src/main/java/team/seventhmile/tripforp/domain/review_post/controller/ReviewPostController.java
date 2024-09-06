package team.seventhmile.tripforp.domain.review_post.controller;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

	/**
	 * 새로운 리뷰 게시글을 작성합니다.
	 *
	 * @param user 현재 인증된 사용자 정보
	 * @param reviewPostDto 작성할 리뷰 게시글 정보
	 * @param files 첨부할 파일 목록 (선택사항)
	 * @return 작성된 리뷰 게시글의 DTO
	 */
	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ReviewPostDto createReviewPost(
		@AuthenticationPrincipal UserDetails user,
		@RequestPart(value = "request") ReviewPostDto reviewPostDto,
		@RequestPart(value = "files", required = false) List<MultipartFile> files
	) throws IOException {
		return reviewPostService.createReviewPost(reviewPostDto, user.getUsername(), files);
	}


	/**
	 * 기존 리뷰 게시글을 수정합니다.
	 *
	 * @param id 수정할 리뷰 게시글의 ID
	 * @param reviewPostDto 수정할 리뷰 게시글 정보
	 * @param user 현재 인증된 사용자 정보
	 * @param files 첨부할 파일 목록 (선택사항)
	 * @return 수정된 리뷰 게시글의 DTO
	 */
	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{id}")
	public ReviewPostDto updateReviewPost(@PathVariable("id") Long id,
		@AuthenticationPrincipal UserDetails user,
		@RequestPart(value = "request") ReviewPostDto reviewPostDto,
		@RequestPart(value = "files", required = false) List<MultipartFile> files
	) throws IOException {
		return reviewPostService.updateReviewPost(id, reviewPostDto, user.getUsername(), files);
	}

	/**
	 * 리뷰 게시글을 삭제합니다.
	 *
	 * @param id 삭제할 리뷰 게시글의 ID
	 * @param user 현재 인증된 사용자 정보
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@DeleteMapping("/{id}")
	public void deleteReviewPost(@PathVariable("id") Long id,
		@AuthenticationPrincipal UserDetails user) {
		reviewPostService.deleteReviewPost(id, user.getUsername());
	}

	/**
	 * 리뷰 게시글 목록을 페이지네이션하여 조회합니다.
	 *
	 * @param page 페이지 번호 (기본값 0)
	 * @param size 페이지 크기 (기본값 10)
	 * @param keyword 검색 키워드 (선택사항)
	 * @return 페이지네이션된 리뷰 게시글 목록
	 */
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

	/**
	 * 특정 리뷰 게시글을 상세 조회합니다.
	 *
	 * @param id 조회할 리뷰 게시글의 ID
	 * @return 조회된 리뷰 게시글의 DTO
	 */
	@GetMapping("/{id}")
	public ReviewPostDto getReviewPostDetail(@PathVariable("id") Long id) {
		return reviewPostService.getReviewPostDetail(id);
	}
}
