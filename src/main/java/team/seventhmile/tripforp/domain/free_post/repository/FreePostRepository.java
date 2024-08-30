package team.seventhmile.tripforp.domain.free_post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.free_post.entity.FreePost;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;

@Repository
public interface FreePostRepository extends JpaRepository<FreePost, Long>, FreePostRepositoryCustom {

}
