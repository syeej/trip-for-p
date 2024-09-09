package team.seventhmile.tripforp.domain.plan.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.plan.dto.GetPlaceCountResponse;
import team.seventhmile.tripforp.domain.plan.dto.QGetPlaceCountResponse;
import team.seventhmile.tripforp.domain.plan.entity.QPlanItem;

@RequiredArgsConstructor
@Repository
public class PlanItemRepositoryImpl implements PlanItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QPlanItem qPlanItem = QPlanItem.planItem;


    @Override
    public List<GetPlaceCountResponse> getPlaceCount(Pageable pageable) {
        return queryFactory
            .select(new QGetPlaceCountResponse(
                qPlanItem.place.id.as("placeId"),
                qPlanItem.count().as("count")
            ))
            .from(qPlanItem)
            .groupBy(qPlanItem.place.id)
            .orderBy(qPlanItem.count().desc(), qPlanItem.place.id.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
}
