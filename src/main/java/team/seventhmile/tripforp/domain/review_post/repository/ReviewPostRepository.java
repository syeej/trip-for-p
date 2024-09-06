package team.seventhmile.tripforp.domain.review_post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;

/**
 * ReviewPost 엔티티에 대한 기본 JPA 레포지토리 인터페이스입니다.
 * 커스텀 쿼리 메서드를 정의하는 ReviewPostRepositoryCustom을 확장합니다.
 */
public interface ReviewPostRepository extends JpaRepository<ReviewPost, Long>,
	ReviewPostRepositoryCustom {
}
