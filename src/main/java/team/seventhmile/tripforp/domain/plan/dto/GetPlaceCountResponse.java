package team.seventhmile.tripforp.domain.plan.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.plan.entity.Place;

@NoArgsConstructor
@Getter
public class GetPlaceCountResponse {

    private PlaceDto place;
    private Long count;

    @QueryProjection
    public GetPlaceCountResponse(Place place, Long count) {
        this.place = new PlaceDto(place);
        this.count = count;
    }
}
