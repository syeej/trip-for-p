package team.seventhmile.tripforp.domain.free_post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.seventhmile.tripforp.domain.free_post.entity.FreePost;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;

public interface FreePostRepositoryCustom {

    Page<FreePost> getFreePosts(Pageable pageable);

    Page<FreePost> getFreePostKeywordContaining(String keyword, Pageable pageable);

}
