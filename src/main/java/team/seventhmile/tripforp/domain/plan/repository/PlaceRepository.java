package team.seventhmile.tripforp.domain.plan.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import team.seventhmile.tripforp.domain.plan.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByAddressNameAndPlaceName(String addressName, String placeName);
}
