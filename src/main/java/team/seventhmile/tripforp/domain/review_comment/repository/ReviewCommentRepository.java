package team.seventhmile.tripforp.domain.review_comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.review_comment.dto.GetReviewCommentDto;
import team.seventhmile.tripforp.domain.review_comment.entity.ReviewComment;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;
import team.seventhmile.tripforp.domain.user.entity.User;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {

	// 특정 ReviewPost 엔티티와 연관된 모든 ReviewComment 엔티티들을 db에서 조회
	Page<ReviewComment> findByReviewPost(ReviewPost reviewPost, Pageable pageable);

	// 특정 ID와 작성자를 기준으로 ReviewComment 엔터티를 조회 (특정 사용자가 작성한 특정 댓글을 조회)
	Optional<ReviewComment> findByIdAndAuthor(Long id, User author);

	//[마이페이지] 리뷰게시글 - 내가 작성한 댓글 목록 조회
    Page<ReviewComment> findByAuthor_Email(String email, Pageable pageable);
}
