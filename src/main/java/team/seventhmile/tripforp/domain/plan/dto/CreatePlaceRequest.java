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
    private double x;
    private double y;

    @Builder
    public CreatePlaceRequest(String addressName, String categoryName, String placeName,
        String placeUrl, double x, double y) {
        this.addressName = addressName;
        this.categoryName = categoryName;
        this.placeName = placeName;
        this.placeUrl = placeUrl;
        this.x = x;
        this.y = y;
    }
}
