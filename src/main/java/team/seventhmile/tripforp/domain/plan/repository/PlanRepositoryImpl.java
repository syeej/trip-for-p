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
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.QPlan;

@RequiredArgsConstructor
@Repository
public class PlanRepositoryImpl implements PlanRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QPlan qPlan = QPlan.plan;

    @Override
    public Page<Plan> getPlans(String area, Pageable pageable) {
        List<Plan> plans = queryFactory.selectFrom(qPlan)
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
        return area != null ? qPlan.area.eq(Area.fromName(area)) : null;
    }
}
