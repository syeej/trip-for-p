package team.seventhmile.tripforp.domain.review_post.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.seventhmile.tripforp.domain.file.entity.MagazineFile;
import team.seventhmile.tripforp.domain.file.entity.ReviewFile;
import team.seventhmile.tripforp.domain.file.service.MagazineFileService;
import team.seventhmile.tripforp.domain.file.service.ReviewFileService;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.repository.PlanRepository;
import team.seventhmile.tripforp.domain.review_post.dto.ReviewPostDto;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;
import team.seventhmile.tripforp.domain.review_post.repository.ReviewPostRepository;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;
import team.seventhmile.tripforp.global.exception.UnauthorizedAccessException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewPostService {

	private final ReviewPostRepository reviewPostRepository;
	private final PlanRepository planRepository;
	private final UserRepository userRepository;
	private final ReviewFileService reviewFileService;

	/**
	 * 리뷰 게시글을 작성합니다.
	 * @param reviewPostDto 게시글 DTO
	 * @param userEmail 작성자 이메일
	 * @param files 첨부 파일 목록
	 * @return 작성된 리뷰 게시글의 DTO
	 */
	@Transactional
	public ReviewPostDto createReviewPost(ReviewPostDto reviewPostDto, String userEmail,
		List<MultipartFile> files) throws IOException {

		// 현재 로그인된 사용자 가져오기
		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException(User.class));

		// Plan 가져오기
		Plan plan = planRepository.findByIdAndUser(reviewPostDto.getPlanId(), user)
			.orElseThrow(() -> new ResourceNotFoundException(Plan.class));

		// 리뷰 게시글 생성
		ReviewPost reviewPost = reviewPostDto.convertToEntity(user, plan);

		// 첨부 파일 처리
		if (files != null && !files.isEmpty()) {
			for (MultipartFile file : files) {
				ReviewFile reviewFile = reviewFileService.saveFile(file);
				reviewPost.addFile(reviewFile);
			}
		}

		reviewPostRepository.save(reviewPost);

		return ReviewPostDto.convertToDto(reviewPost);
	}

	/**
	 * 리뷰 게시글을 수정합니다.
	 * 작성자만 수정할 수 있습니다.
	 * @param id 수정할 게시글의 ID
	 * @param reviewPostDto 수정할 게시글의 DTO
	 * @param userEmail 작성자 이메일
	 * @param files 첨부 파일 목록
	 * @return 수정된 리뷰 게시글의 DTO
	 */
	@Transactional
	public ReviewPostDto updateReviewPost(Long id,
		ReviewPostDto reviewPostDto, String userEmail, List<MultipartFile> files) throws IOException {

		ReviewPost reviewPost = reviewPostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ReviewPost.class));

		// 현재 로그인된 사용자 가져오기
		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException(User.class));

		// 작성자 확인
		if (!reviewPost.getUser().getEmail().equals(userEmail)) {
			throw new UnauthorizedAccessException(ReviewPost.class);
		}

		// 게시글 수정
		reviewPost.update(reviewPostDto.getTitle(), reviewPostDto.getContent());
		if (reviewPost.getFiles() != null) {
			for (ReviewFile file : reviewPost.getFiles()) {
				reviewFileService.deleteFile(file.getFileName());
			}
		}
		reviewPost.clearFile();

		if (files != null && !files.isEmpty()) {
			for (MultipartFile file : files) {
				ReviewFile reviewFile = reviewFileService.saveFile(file);
				reviewPost.addFile(reviewFile);
			}
		}

		return reviewPostDto.convertToDto(reviewPost);
	}

	/**
	 * 리뷰 게시글을 삭제합니다.
	 * 작성자 또는 ADMIN만 삭제할 수 있습니다.
	 * @param id 삭제할 게시글의 ID
	 * @param userEmail 요청자 이메일
	 */
	@Transactional
	public void deleteReviewPost(Long id, String userEmail) {

		ReviewPost reviewPost = reviewPostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ReviewPost.class));

		// 현재 로그인된 사용자 가져오기
		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException(User.class));

		// 권한 확인 (작성자 또는 ADMIN)
		if (!reviewPost.getUser().getEmail().equals(userEmail) && user.getRole() != Role.ADMIN) {
			throw new UnauthorizedAccessException(ReviewPost.class);
		}

		if (reviewPost.getFiles() != null) {
			for (ReviewFile file : reviewPost.getFiles()) {
				reviewFileService.deleteFile(file.getFileName());
			}
		}

		reviewPostRepository.delete(reviewPost);
	}

	/**
	 * 모든 리뷰 게시글을 페이지네이션하여 조회합니다.
	 * @param pageable 페이징 정보
	 * @return 페이지네이션된 리뷰 게시글 목록
	 */
	@Transactional(readOnly = true)
	public Page<ReviewPostDto> getAllReviewPost(Pageable pageable) {

		return reviewPostRepository.getReviewPosts(pageable)
			.map(ReviewPostDto::convertToDto);
	}

	/**
	 * 특정 ID의 리뷰 게시글을 상세 조회합니다.
	 * 조회할 때마다 조회 수가 증가합니다.
	 * @param id 조회할 게시글의 ID
	 * @return 리뷰 게시글 DTO
	 */
	@Transactional
	public ReviewPostDto getReviewPostDetail(Long id) {
		ReviewPost reviewPost = reviewPostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ReviewPost.class));

		// 조회 수 증가
		reviewPost.incrementViews();

		return ReviewPostDto.convertToDto(reviewPost);
	}

	/**
	 * 키워드를 포함하는 리뷰 게시글을 페이지네이션하여 검색합니다.
	 * @param keyword 검색 키워드
	 * @param pageable 페이징 정보
	 * @return 검색된 리뷰 게시글 목록
	 */
	@Transactional(readOnly = true)
	public Page<ReviewPostDto> getReviewPostSearch(String keyword, Pageable pageable) {

		if (keyword == null || keyword.trim().isEmpty()) {
			return Page.empty(pageable);
		}

		Page<ReviewPost> reviewPosts = reviewPostRepository.getReviewPostKeywordContaining(
			keyword.trim(), pageable);
		return reviewPosts.map(ReviewPostDto::convertToDto);
	}

	/**
	 * 특정 ID의 리뷰 게시글 엔티티를 조회합니다.
	 * @param id 조회할 게시글의 ID
	 * @return 리뷰 게시글 엔티티
	 */
	@Transactional(readOnly = true)
	public ReviewPost getReviewPostEntity(Long id) {
		return reviewPostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ReviewPost.class));
	}

}
