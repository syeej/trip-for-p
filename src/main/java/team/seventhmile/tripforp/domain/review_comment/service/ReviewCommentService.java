package team.seventhmile.tripforp.domain.review_comment.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.free_comment.entity.FreeComment;
import team.seventhmile.tripforp.domain.review_comment.dto.GetReviewCommentDto;
import team.seventhmile.tripforp.domain.review_comment.dto.ReviewCommentDto;
import team.seventhmile.tripforp.domain.review_comment.entity.ReviewComment;
import team.seventhmile.tripforp.domain.review_comment.repository.ReviewCommentRepository;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;
import team.seventhmile.tripforp.global.exception.UnauthorizedAccessException;

@Service
@Transactional(readOnly = true)
public class ReviewCommentService {

	private final ReviewCommentRepository reviewCommentRepository;
	private final UserRepository userRepository;

	@Autowired
	public ReviewCommentService(ReviewCommentRepository reviewCommentRepository,
		UserRepository userRepository) {
		this.reviewCommentRepository = reviewCommentRepository;
		this.userRepository = userRepository;
	}

	// 리뷰 게시판 댓글 조회
	public Page<GetReviewCommentDto> getCommentsByPost(ReviewPost reviewPost, Pageable pageable) {
		Page<ReviewComment> comments = reviewCommentRepository.findByReviewPost(reviewPost, pageable);
		return comments.map(GetReviewCommentDto::new);
	}

	// 리뷰 게시판 댓글 작성
	@Transactional
	public ReviewCommentDto createComment(ReviewPost reviewPost, ReviewCommentDto reviewCommentDto,
		UserDetails user) {
		User findUser = getUser(user);
		ReviewComment reviewComment = mapToEntity(reviewCommentDto);
		reviewComment.setReviewPost(reviewPost);
		reviewComment.setAuthor(findUser);
		ReviewComment savedComment = reviewCommentRepository.save(reviewComment);
		return mapToDto(savedComment);
	}

	// 리뷰 게시판 댓글 수정
	@Transactional
	public ReviewCommentDto updateComment(Long id, ReviewCommentDto updatedCommentDto, UserDetails user) {
		User findUser = getUser(user);
		ReviewComment reviewComment = reviewCommentRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ReviewComment.class, id));

		if (!reviewComment.getAuthor().getId().equals(findUser.getId())) {
			throw new UnauthorizedAccessException(FreeComment.class);
		}
		reviewComment.setContent(updatedCommentDto.getContent());
		ReviewComment updatedComment = reviewCommentRepository.save(reviewComment);
		return mapToDto(updatedComment);
	}

	// 리뷰 게시판 댓글 삭제
	@Transactional
	public void deleteComment(Long id, UserDetails user) {
		User findUser = getUser(user);
		ReviewComment reviewComment = reviewCommentRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ReviewComment.class, id));

		if (!reviewComment.getAuthor().getId().equals(findUser.getId()) && !reviewComment.getReviewPost()
			.getUser().getId().equals(findUser.getId()) && findUser.getRole() != Role.ADMIN) {
			throw new UnauthorizedAccessException(FreeComment.class);
		}
		reviewCommentRepository.delete(reviewComment);
	}


	// Entity -> Dto 변환
	private ReviewCommentDto mapToDto(ReviewComment reviewComment) {
		return ReviewCommentDto.builder()
			.id(reviewComment.getId())
			.content(reviewComment.getContent())
			.postId(reviewComment.getReviewPost().getId())
			.authorId(reviewComment.getAuthor().getId())
			.build();
	}

	// Dto -> Entity 변환
	private ReviewComment mapToEntity(ReviewCommentDto reviewCommentDto) {
		ReviewComment reviewComment = new ReviewComment();
		reviewComment.setContent(reviewCommentDto.getContent());
		return reviewComment;
	}

	private User getUser(UserDetails user) {
		return userRepository.findByEmail(user.getUsername())
			.orElseThrow(() -> new ResourceNotFoundException(User.class));
	}

	//[마이페이지] 리뷰게시글 내가 작성한 댓글 목록 조회
	@Transactional(readOnly = true)
    public Page<ReviewCommentDto> getMyreviewCommentList(UserDetails user, Pageable pageable) {
		return reviewCommentRepository.findByAuthor_Email(user.getUsername(), pageable)
				.map(ReviewCommentDto::convertToDto);
    }
}
