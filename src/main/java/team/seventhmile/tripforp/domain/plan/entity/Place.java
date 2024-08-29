package team.seventhmile.tripforp.domain.plan.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "places")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long placeId;

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

    // @Builder를 사용한 커스텀 생성자
    @Builder
    public Place(String addr1, String addr2, Long areaCode, String cat1, String cat2, String cat3, Integer contentId, Integer contentTypeId, Double mapX, Double mapY, Integer sigunguCode, String tel, String title, String cpyrhtDivCd, String firstImage, String firstImage2, Integer selectedCount) {
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.areaCode = areaCode;
        this.cat1 = cat1;
        this.cat2 = cat2;
        this.cat3 = cat3;
        this.contentId = contentId;
        this.contentTypeId = contentTypeId;
        this.mapX = mapX;
        this.mapY = mapY;
        this.sigunguCode = sigunguCode;
        this.tel = tel;
        this.title = title;
        this.cpyrhtDivCd = cpyrhtDivCd;
        this.firstImage = firstImage;
        this.firstImage2 = firstImage2;
        this.selectedCount = selectedCount;
    }
}


