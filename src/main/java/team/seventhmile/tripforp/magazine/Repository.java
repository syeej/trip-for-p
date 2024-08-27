package team.seventhmile.tripforp.magazine;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface Repository extends JpaRepository<Entity, Long> {

	List<Entity> findAllByOrderByIdDesc();

	Optional<Entity> findById(Long id);
}
