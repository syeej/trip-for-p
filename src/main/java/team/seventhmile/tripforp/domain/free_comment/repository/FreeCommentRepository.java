package team.seventhmile.tripforp.domain.free_comment.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.free_comment.entity.FreeComment;
import team.seventhmile.tripforp.domain.free_post.entity.FreePost;
import team.seventhmile.tripforp.domain.user.entity.User;

public interface FreeCommentRepository extends JpaRepository<FreeComment, Long> {
	// 특정 FreePost 엔티티와 연관된 모든 FreeComment 엔티티들을 db에서 조회
	Page<FreeComment> findByFreePost(FreePost freePost, Pageable pageable);

	// 특정 ID와 작성자를 기준으로 FreeComment 엔터티를 조회 (특정 사용자가 작성한 특정 댓글을 조회)
	Optional<FreeComment> findByIdAndAuthor(Long id, User author);

	//[마이페이지] 자유게시글 - 내가 작성한 댓글 목록 조회
    Page<FreeComment> findByAuthor_Email(String username, Pageable pageable);
}
