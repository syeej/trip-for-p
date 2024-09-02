package team.seventhmile.tripforp.domain.plan.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.plan.dto.GetPlanListResponse;
import team.seventhmile.tripforp.domain.plan.dto.QGetPlanListResponse;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.QPlan;

@RequiredArgsConstructor
@Repository
public class PlanRepositoryImpl implements PlanRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QPlan qPlan = QPlan.plan;

    @Override
    public Page<GetPlanListResponse> getPlans(String area, Pageable pageable) {
        System.out.println(Area.fromName(area));
        List<GetPlanListResponse> plans = queryFactory
            .select(new QGetPlanListResponse(qPlan))
            .from(qPlan)
            .where(equalArea(area))
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        JPAQuery<Long> count = queryFactory
            .select(qPlan.count())
            .from(qPlan)
            .where(equalArea(area));

        return PageableExecutionUtils.getPage(plans, pageable, count::fetchOne);
    }

    private BooleanExpression equalArea(String area) {
        if (area == null || area.isEmpty()) {
            return null;
        }
        if (Area.fromName(area) == null) {
            return qPlan.area.isNull();
        }
        return qPlan.area.eq(Area.fromName(area));
    }
}
