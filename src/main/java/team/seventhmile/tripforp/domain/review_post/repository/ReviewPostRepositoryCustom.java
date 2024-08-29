package team.seventhmile.tripforp.domain.review_post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;

public interface ReviewPostRepositoryCustom {

    Page<ReviewPost> getReviewPosts(Pageable pageable);

    Page<ReviewPost> getReviewPostKeywordContaining(String keyword, Pageable pageable);

}
