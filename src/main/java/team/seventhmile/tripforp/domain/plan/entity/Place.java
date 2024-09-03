package team.seventhmile.tripforp.domain.plan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Getter
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
    private double x;
    private double y;

    @Builder
    public Place(String addressName, String categoryName, String placeName, String placeUrl, double x, double y) {
        this.addressName = addressName;
        this.categoryName = categoryName;
        this.placeName = placeName;
        this.placeUrl = placeUrl;
        this.x = x;
        this.y = y;
    }
}


