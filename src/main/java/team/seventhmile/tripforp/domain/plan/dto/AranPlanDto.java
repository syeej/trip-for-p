package team.seventhmile.tripforp.domain.plan.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class AranPlanDto {
    private String planTitle;
    private String planArea;
    private List<String> placeNames;  // PlanItems의 장소 이름
    private List<String> likedPlanTitles;  // 좋아요한 Plan의 제목
    private List<String> likedPlanAreas;  // 좋아요한 Plan의 지역

    public AranPlanDto(String planTitle, String planArea, List<String> placeNames,
                            List<String> likedPlanTitles, List<String> likedPlanAreas) {
        this.planTitle = planTitle;
        this.planArea = planArea;
        this.placeNames = placeNames;
        this.likedPlanTitles = likedPlanTitles;
        this.likedPlanAreas = likedPlanAreas;
    }

    // getters and setters
}
