package team.seventhmile.tripforp.domain.plan.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.plan.entity.Plan;

@NoArgsConstructor
@Getter
public class GetPopularPlanResponse {

    private Long id;
    private String title;
    private long likeCount;
    private String imageUrl;

    @QueryProjection
    public GetPopularPlanResponse(Plan plan, long likeCount, String imageUrl) {
        this.id = plan.getId();
        this.title = plan.getTitle();
        this.likeCount = likeCount;
        this.imageUrl = imageUrl;
    }
}
