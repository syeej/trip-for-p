package team.seventhmile.tripforp.domain.review_post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.QPlan;
import team.seventhmile.tripforp.domain.review_post.entity.QReviewPost;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;

import java.util.List;
import team.seventhmile.tripforp.domain.user.entity.User;

/**
 * ReviewPostRepositoryCustom 인터페이스의 구현체입니다.
 * QueryDSL을 사용하여 복잡한 쿼리를 수행합니다.
 */
@RequiredArgsConstructor
@Repository
public class ReviewPostRepositoryImpl implements ReviewPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReviewPost qReviewPost = QReviewPost.reviewPost;
    private final QPlan qPlan = QPlan.plan;

    /**
     * 페이징된 ReviewPost 목록을 가져옵니다.
     *
     * @param pageable 페이징 정보
     * @return 페이징된 ReviewPost 페이지 객체
     */
    @Override
    public Page<ReviewPost> getReviewPosts(Pageable pageable) {
        // ReviewPost 엔티티를 기준으로 페이징된 목록을 조회합니다.
        List<ReviewPost> reviewPosts = queryFactory.selectFrom(qReviewPost)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        // 전체 레코드 수를 카운트합니다.
        JPAQuery<Long> count = queryFactory
                .select(qReviewPost.count())
                .from(qReviewPost);

        // 페이징된 결과를 반환합니다.
        return PageableExecutionUtils.getPage(reviewPosts, pageable, count::fetchOne);

    }

    /**
     * 주어진 키워드를 포함하는 ReviewPost를 검색합니다.
     *
     * @param keyword 검색할 키워드
     * @param pageable 페이징 정보
     * @return 페이징된 검색 결과 ReviewPost 페이지 객체
     */
    @Override
    public Page<ReviewPost> getReviewPostKeywordContaining(String keyword, Pageable pageable) {
        // 제목이나 내용에 키워드가 포함된 레코드를 조회합니다.
        BooleanExpression searchKeyword = qReviewPost.title.containsIgnoreCase(keyword)
                .or(qReviewPost.content.containsIgnoreCase(keyword));

        List<ReviewPost> reviewPosts = queryFactory.selectFrom(qReviewPost)
                .where(searchKeyword)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        // 검색 결과에 대한 전체 레코드 수를 카운트합니다.
        JPAQuery<Long> count = queryFactory
                .select(qReviewPost.count())
                .from(qReviewPost)
                .where(searchKeyword);

        // 페이징된 검색 결과를 반환합니다.
        return PageableExecutionUtils.getPage(reviewPosts, pageable, count::fetchOne);
    }

    /**
     * 특정 사용자가 작성한 Plan 목록을 조회합니다.
     *
     * @param user 조회할 사용자
     * @return 사용자가 작성한 Plan 목록
     */
    @Override
    public List<Plan> findUserPlans(User user) {
        return queryFactory.selectFrom(qPlan)
            .where(qPlan.user.eq(user))
            .fetch();
    }
}
