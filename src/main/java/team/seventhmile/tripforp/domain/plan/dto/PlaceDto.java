package team.seventhmile.tripforp.domain.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.plan.entity.Place;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PlaceDto {

    private Long id;
    private String addressName;
    private String categoryName;
    private String placeName;
    private String imageUrl;
    private double x;
    private double y;

    public PlaceDto(Place place) {
        this.id = place.getId();
        this.addressName = place.getAddressName();
        this.categoryName = place.getCategoryName();
        this.placeName = place.getPlaceName();
        this.imageUrl = place.getImageUrl();
        this.x = place.getX();
        this.y = place.getY();
    }
}
