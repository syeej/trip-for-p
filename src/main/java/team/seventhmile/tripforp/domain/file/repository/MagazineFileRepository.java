package team.seventhmile.tripforp.domain.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.file.entity.MagazineFile;

@Repository
public interface MagazineFileRepository extends JpaRepository<MagazineFile, Long> {

}
