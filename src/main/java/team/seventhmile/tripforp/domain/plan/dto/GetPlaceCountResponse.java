package team.seventhmile.tripforp.domain.plan.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GetPlaceCountResponse {

    private Long placeId;
    private Long count;

    @QueryProjection
    public GetPlaceCountResponse(Long placeId, Long count) {
        this.placeId = placeId;
        this.count = count;
    }
}
