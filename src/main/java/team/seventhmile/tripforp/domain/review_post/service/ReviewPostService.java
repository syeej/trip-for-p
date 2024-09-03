package team.seventhmile.tripforp.domain.review_post.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.seventhmile.tripforp.domain.file.entity.File;
import team.seventhmile.tripforp.domain.file.service.FileService;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.repository.PlanRepository;
import team.seventhmile.tripforp.domain.review_post.dto.ReviewPostDto;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;
import team.seventhmile.tripforp.domain.review_post.repository.ReviewPostRepository;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewPostService {

	private final ReviewPostRepository reviewPostRepository;
	private final PlanRepository planRepository;
	private final JPAQueryFactory jpaQueryFactory;
	private final UserRepository userRepository;
	private final FileService fileService;

	// 리뷰 게시글 작성
	@Transactional
	public ReviewPostDto createReviewPost(ReviewPostDto reviewPostDto, Long userId,
		List<MultipartFile> files) {

		// 현재 로그인된 사용자 가져오기
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("User not found"));

		// Plan 가져오기
		Plan plan = planRepository.findById(reviewPostDto.getPlanId())
			.orElseThrow(() -> new IllegalArgumentException("Plan not found"));

		// 파일 저장 로직
		List<File> savedFiles = (files != null & !files.isEmpty() ? fileService.saveFiles(files)
			: Collections.emptyList());

		// 리뷰 게시글 생성 및 저장
		ReviewPost reviewPost = reviewPostDto.convertToEntity(user, plan);
		reviewPost.setFiles(savedFiles);

		reviewPostRepository.save(reviewPost);

		return ReviewPostDto.convertToDto(reviewPost);
	}

	// 리뷰 게시글 수정
	@Transactional
	public ReviewPostDto updateReviewPost(Long id,
		ReviewPostDto reviewPostDto, Long userId, List<MultipartFile> files) {

		ReviewPost reviewPost = reviewPostRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("데이터를 찾을 수 없습니다."));

		// 현재 로그인된 사용자 가져오기
		User user = userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("User not found"));

		// 작성자 확인
		if (!reviewPost.getUser().getId().equals(userId)) {
			throw new SecurityException("본인의 글만 수정할 수 있습니다.");
		}

		// 파일 저장 로직
		List<File> savedFiles = (files != null && !files.isEmpty() ? fileService.saveFiles(files)
			: Collections.emptyList());

		// 업데이트
		reviewPost.update(reviewPostDto.getTitle(), reviewPostDto.getContent(), savedFiles);

		reviewPostRepository.save(reviewPost);
		return reviewPostDto.convertToDto(reviewPost);
	}

	// 리뷰 게시글 삭제
	@Transactional
	public void deleteReviewPost(Long id, Long userId) {

		ReviewPost reviewPost = reviewPostRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("데이터를 찾을 수 없습니다."));

		// 현재 로그인된 사용자 가져오기
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		// 권한 확인 (작성자 또는 ADMIN)
		if (!reviewPost.getUser().getId().equals(userId) && user.getRole() != Role.ADMIN) {
			throw new SecurityException("삭제 권한이 없습니다.");
		}

		reviewPostRepository.delete(reviewPost);
	}

	// 리뷰 게시글 목록 조회
	@Transactional(readOnly = true)
	public Page<ReviewPostDto> getAllReviewPost(Pageable pageable) {

		return reviewPostRepository.getReviewPosts(pageable)
			.map(ReviewPostDto::convertToDto);

	}

	// 리뷰 게시글 상세 조회
	@Transactional(readOnly = true)
	public ReviewPostDto getReviewPostDetail(Long id) {

		return reviewPostRepository.findById(id)
			.map(ReviewPostDto::convertToDto)
			.orElseThrow(() -> new EntityNotFoundException("데이터를 찾을 수 없습니다."));

	}

	// 리뷰 게시글 검색(제목, 내용) 조회
	@Transactional(readOnly = true)
	public Page<ReviewPostDto> getReviewPostSearch(String keyword, Pageable pageable) {

		if (keyword == null || keyword.trim().isEmpty()) {
			return Page.empty(pageable);
		}

		Page<ReviewPost> reviewPosts = reviewPostRepository.getReviewPostKeywordContaining(
			keyword.trim(), pageable);
		return reviewPosts.map(ReviewPostDto::convertToDto);

	}

	// ReviewPost 엔티티 조회
	@Transactional(readOnly = true)
	public ReviewPost getReviewPostEntity(Long id) {
		return reviewPostRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("데이터를 찾을 수 없습니다."));
	}

}
