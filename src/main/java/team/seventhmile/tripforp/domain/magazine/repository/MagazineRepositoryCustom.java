package team.seventhmile.tripforp.domain.magazine.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import team.seventhmile.tripforp.domain.magazine.entity.Magazine;

public interface MagazineRepositoryCustom {

	Page<Magazine> getMagazinePosts(Pageable pageable);

	Page<Magazine> getMagazineKeywordContaining(String keyword, Pageable pageable);

}
