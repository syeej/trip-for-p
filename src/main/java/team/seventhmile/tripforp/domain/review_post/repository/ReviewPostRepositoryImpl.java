package team.seventhmile.tripforp.domain.review_post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.review_post.entity.QReviewPost;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ReviewPostRepositoryImpl implements ReviewPostRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QReviewPost qReviewPost = QReviewPost.reviewPost;

    @Override
    public Page<ReviewPost> getReviewPosts(Pageable pageable) {
        List<ReviewPost> reviewPosts = queryFactory.selectFrom(qReviewPost)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(qReviewPost.count())
                .from(qReviewPost);

        return PageableExecutionUtils.getPage(reviewPosts, pageable, count::fetchOne);

    }

    @Override
    public Page<ReviewPost> getReviewPostKeywordContaining(String keyword, Pageable pageable) {
        BooleanExpression searchKeyword = qReviewPost.title.containsIgnoreCase(keyword)
                .or(qReviewPost.content.containsIgnoreCase(keyword));

        List<ReviewPost> reviewPosts = queryFactory.selectFrom(qReviewPost)
                .where(searchKeyword)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(qReviewPost.count())
                .from(qReviewPost)
                .where(searchKeyword);

        return PageableExecutionUtils.getPage(reviewPosts, pageable, count::fetchOne);
    }


}
