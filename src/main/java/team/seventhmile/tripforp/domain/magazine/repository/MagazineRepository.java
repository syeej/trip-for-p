package team.seventhmile.tripforp.domain.magazine.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.magazine.entity.Magazine;

public interface MagazineRepository extends JpaRepository<Magazine, Long> {

    List<Magazine> findAllByOrderByIdDesc();

    Optional<Magazine> findById(Long id);
}

