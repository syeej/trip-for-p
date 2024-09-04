package team.seventhmile.tripforp.domain.review_post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;

public interface ReviewPostRepository extends JpaRepository<ReviewPost, Long>,
	ReviewPostRepositoryCustom {
}
