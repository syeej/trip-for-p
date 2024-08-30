package team.seventhmile.tripforp.domain.plan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "places")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long id;

    private String addressName;
    private String categoryName;
    private String placeName;
    private String placeUrl;

    @Column(name = "mapx", nullable = false)
    private Double mapX;

    @Column(name = "mapy", nullable = false)
    private Double mapY;

    @Builder
    public Place(String addressName, String categoryName, String placeName, String placeUrl) {
        this.addressName = addressName;
        this.categoryName = categoryName;
        this.placeName = placeName;
        this.placeUrl = placeUrl;
    }


}


