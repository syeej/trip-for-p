package team.seventhmile.tripforp.domain.review_comment.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.seventhmile.tripforp.domain.review_comment.dto.ReviewCommentDto;
import team.seventhmile.tripforp.domain.review_comment.entity.ReviewComment;
import team.seventhmile.tripforp.domain.review_comment.repository.ReviewCommentRepository;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;
import team.seventhmile.tripforp.domain.user.entity.User;

@Service
public class ReviewCommentService {

	private final ReviewCommentRepository reviewCommentRepository;

	@Autowired
	public ReviewCommentService(ReviewCommentRepository reviewCommentRepository) {
		this.reviewCommentRepository = reviewCommentRepository;
	}

	// 리뷰 게시판 댓글 조회
	public List<ReviewCommentDto> getCommentsByPost(ReviewPost reviewPost) {
		List<ReviewComment> comments = reviewCommentRepository.findByReviewPost(reviewPost);
		return comments.stream()
			.map(this::mapToDto)
			.collect(Collectors.toList());
	}

	// 리뷰 게시판 댓글 작성
	public ReviewCommentDto createComment(ReviewPost reviewPost, ReviewCommentDto reviewCommentDto,
		User user) {
		ReviewComment reviewComment = mapToEntity(reviewCommentDto);
		reviewComment.setReviewPost(reviewPost);
		reviewComment.setAuthor(user);
		ReviewComment savedComment = reviewCommentRepository.save(reviewComment);
		return mapToDto(savedComment);
	}

	// 리뷰 게시판 댓글 수정
	public ReviewCommentDto updateComment(Long id, ReviewCommentDto updatedCommentDto, User user) {
		ReviewComment existingComment = reviewCommentRepository.findByIdAndAuthor(id, user)
			.orElseThrow(() -> new RuntimeException("Comment not found or not owned by user"));

		existingComment.setContent(updatedCommentDto.getContent());
		ReviewComment updatedComment = reviewCommentRepository.save(existingComment);
		return mapToDto(updatedComment);
	}

	// 리뷰 게시판 댓글 삭제
	public void deleteComment(Long id, User user) {
		ReviewComment reviewComment = reviewCommentRepository.findByIdAndAuthor(id, user)
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
}
