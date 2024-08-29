package team.seventhmile.tripforp.domain.plan.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.plan.entity.Place;

@NoArgsConstructor
@Getter
public class CreatePlaceRequest {

    private String addressName;
    private String categoryName;
    private String placeName;
    private String placeUrl;

    @Builder
    public CreatePlaceRequest(String addressName, String categoryName, String placeName,
        String placeUrl) {
        this.addressName = addressName;
        this.categoryName = categoryName;
        this.placeName = placeName;
        this.placeUrl = placeUrl;
    }
}
