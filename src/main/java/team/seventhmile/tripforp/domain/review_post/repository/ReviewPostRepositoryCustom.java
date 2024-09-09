package team.seventhmile.tripforp.domain.review_post.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;
import team.seventhmile.tripforp.domain.user.entity.User;

public interface ReviewPostRepositoryCustom {

	Page<ReviewPost> getReviewPosts(Pageable pageable);

	Page<ReviewPost> getReviewPostKeywordContaining(String keyword, Pageable pageable);

	// 사용자가 작성한 Plan 조회
	List<Plan> findUserPlans(User user);

	//[마이페이지] 내가 작성한 리뷰 게시글 목록 조회
	Page<ReviewPost> getMyReviews(String email, Pageable pageable);
}
