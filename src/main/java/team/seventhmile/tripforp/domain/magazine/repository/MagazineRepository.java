package team.seventhmile.tripforp.domain.magazine.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.magazine.entity.Magazine;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;

public interface MagazineRepository extends JpaRepository<Magazine, Long>,
	MagazineRepositoryCustom {

	Page<Magazine> findAll(Pageable pageable);
}

