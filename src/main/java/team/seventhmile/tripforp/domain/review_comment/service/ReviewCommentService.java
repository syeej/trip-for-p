package team.seventhmile.tripforp.domain.review_comment.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.review_comment.dto.ReviewCommentDto;
import team.seventhmile.tripforp.domain.review_comment.entity.ReviewComment;
import team.seventhmile.tripforp.domain.review_comment.repository.ReviewCommentRepository;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;

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
	public List<ReviewCommentDto> getCommentsByPost(ReviewPost reviewPost) {
		List<ReviewComment> comments = reviewCommentRepository.findByReviewPost(reviewPost);
		return comments.stream()
			.map(this::mapToDto)
			.collect(Collectors.toList());
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
		ReviewComment existingComment = reviewCommentRepository.findByIdAndAuthor(id, findUser)
			.orElseThrow(() -> new RuntimeException("Comment not found or not owned by user"));

		existingComment.setContent(updatedCommentDto.getContent());
		ReviewComment updatedComment = reviewCommentRepository.save(existingComment);
		return mapToDto(updatedComment);
	}

	// 리뷰 게시판 댓글 삭제
	@Transactional
	public void deleteComment(Long id, UserDetails user) {
		User findUser = getUser(user);
		ReviewComment reviewComment = reviewCommentRepository.findByIdAndAuthor(id, findUser)
			.orElseThrow(() -> new RuntimeException("Comment not found or not owned by user"));
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

	//[마이페이지] 내가 작성한 리뷰 댓글 목록 조회
    public Page<ReviewCommentDto> getMyreviewCommentList(UserDetails user, Pageable pageable) {
		return reviewCommentRepository.findByAuthor_Email(user.getUsername(), pageable)
				.map(ReviewCommentDto::convertToDto);
    }
}
