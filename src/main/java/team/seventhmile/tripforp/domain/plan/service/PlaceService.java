package team.seventhmile.tripforp.domain.plan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlaceRequest;
import team.seventhmile.tripforp.domain.plan.entity.Place;
import team.seventhmile.tripforp.domain.plan.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final PlaceRepository placeRepository;

    @Transactional
    public Place FindOrCreatePlace(CreatePlaceRequest request) {
        return placeRepository.findByAddressNameAndPlaceName(
                request.getAddressName(),
                request.getPlaceName())
            .orElseGet(() -> {
                Place newPlace = Place.builder()
                    .addressName(request.getAddressName())
                    .categoryName(request.getCategoryName())
                    .placeName(request.getPlaceName())
                    .placeUrl(request.getPlaceUrl())
                    .build();
                return placeRepository.save(newPlace);
            });
    }

}
