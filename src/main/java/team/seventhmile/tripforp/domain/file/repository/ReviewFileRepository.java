package team.seventhmile.tripforp.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.file.entity.ReviewFile;

@Repository
public interface ReviewFileRepository extends JpaRepository<ReviewFile, Long> {

}
