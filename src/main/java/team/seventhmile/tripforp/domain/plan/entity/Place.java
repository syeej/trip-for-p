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

    @Column(name = "addr1", nullable = false, length = 255)
    private String addr1;

    @Column(name = "addr2", length = 255)
    private String addr2;

    @Column(name = "areacode", nullable = false)
    private Long areaCode;

    @Column(name = "cat1", nullable = false, length = 10)
    private String cat1;

    @Column(name = "cat2", length = 10)
    private String cat2;

    @Column(name = "cat3", length = 10)
    private String cat3;

    @Column(name = "contentid", nullable = false)
    private Integer contentId;

    @Column(name = "contenttypeid", nullable = false)
    private Integer contentTypeId;

    @Column(name = "mapx", nullable = false)
    private Double mapX;

    @Column(name = "mapy", nullable = false)
    private Double mapY;

    @Column(name = "sigungucode", nullable = false)
    private Integer sigunguCode;

    @Column(name = "tel", length = 15)
    private String tel;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "cpyrhtDivCd", nullable = false, length = 5)
    private String cpyrhtDivCd;

    @Column(name = "firstimage")
    private String firstImage;

    @Column(name = "firstimage2")
    private String firstImage2;

    @Column(name = "selected_count", nullable = false)
    private Integer selectedCount;
}


