package team.seventhmile.tripforp.domain.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceGetDto {
    private Long placeId;
    private String title;
    private String address;
    private Double mapX;
    private Double mapY;
    private String tel;
    private String firstImage;
    private String firstImage2;
}