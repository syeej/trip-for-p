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
//    private String author;
    private String title;
    private String area;
    private ZonedDateTime createdAt;
    private int views;
    private int likes;

    @QueryProjection
    public GetPlanListResponse(Plan plan) {
        this.id = plan.getId();
//        this.author = plan.getUser().getNickname();
        this.title = plan.getTitle();
        this.area = plan.getArea().getName();
        this.createdAt = plan.getCreatedAt();
        this.views = plan.getViews();
        this.likes = plan.getPlanLikes().size();
    }
}
