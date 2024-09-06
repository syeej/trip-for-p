package team.seventhmile.tripforp.domain.review_post.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;
import team.seventhmile.tripforp.domain.user.entity.User;

/**
 * 커스텀 메서드를 정의하기 위한 ReviewPost 레포지토리 인터페이스입니다.
 * QueryDSL 등을 사용하여 복잡한 쿼리 메서드를 정의합니다.
 */
public interface ReviewPostRepositoryCustom {

	/**
	 * 페이징된 ReviewPost 목록을 가져옵니다.
	 *
	 * @param pageable 페이징 정보
	 * @return 페이징된 ReviewPost 페이지 객체
	 */
	Page<ReviewPost> getReviewPosts(Pageable pageable);

	/**
	 * 주어진 키워드를 포함하는 ReviewPost를 검색합니다.
	 *
	 * @param keyword 검색할 키워드
	 * @param pageable 페이징 정보
	 * @return 페이징된 검색 결과 ReviewPost 페이지 객체
	 */
	Page<ReviewPost> getReviewPostKeywordContaining(String keyword, Pageable pageable);

	/**
	 * 특정 사용자가 작성한 Plan 목록을 조회합니다.
	 *
	 * @param user 조회할 사용자
	 * @return 사용자가 작성한 Plan 목록
	 */
	List<Plan> findUserPlans(User user);

}
