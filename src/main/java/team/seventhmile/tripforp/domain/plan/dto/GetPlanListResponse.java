package team.seventhmile.tripforp.domain.plan.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.plan.entity.Plan;

@NoArgsConstructor
@Getter
public class GetPlanListResponse {

    private Long id;
    private String writer;
    private String title;
    private String area;
    private ZonedDateTime createdAt;
    private int views;
    private long likes;

    @QueryProjection
    public GetPlanListResponse(Plan plan, long likes) {
        this.id = plan.getId();
        this.writer = plan.getUser().getNickname();
        this.title = plan.getTitle();
        this.area = plan.getArea().getName();
        this.createdAt = plan.getCreatedAt();
        this.views = plan.getViews();
        this.likes = likes;
    }
}
