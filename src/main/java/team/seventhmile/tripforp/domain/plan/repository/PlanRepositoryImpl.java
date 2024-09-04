package team.seventhmile.tripforp.domain.plan.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
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
import team.seventhmile.tripforp.domain.plan.entity.QPlanItem;
import team.seventhmile.tripforp.domain.plan.entity.QPlanLike;

@RequiredArgsConstructor
@Repository
public class PlanRepositoryImpl implements PlanRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QPlan qPlan = QPlan.plan;
    private final QPlanItem qPlanItem = QPlanItem.planItem;
    private final QPlanLike qPlanLike = QPlanLike.planLike;

    @Override
    public Page<GetPlanListResponse> getPlans(String area, Pageable pageable) {
        List<GetPlanListResponse> plans = queryFactory
            .select(new QGetPlanListResponse(
                qPlan,
                JPAExpressions
                    .select(qPlanLike.count())
                    .from(qPlanLike)
                    .where(qPlanLike.plan.eq(qPlan))
            ))
            .from(qPlan)
            .where(equalArea(area))
            .leftJoin(qPlan.user).fetchJoin()
            .leftJoin(qPlan.planLikes)
            .orderBy(qPlan.createdAt.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        JPAQuery<Long> count = queryFactory
            .select(qPlan.count())
            .from(qPlan)
            .where(equalArea(area));

        return PageableExecutionUtils.getPage(plans, pageable, count::fetchOne);
    }

    @Override
    public Page<GetPlanListResponse> getMyPlans(String email, Pageable pageable) {
        List<GetPlanListResponse> plans = queryFactory
            .select(new QGetPlanListResponse(
                qPlan,
                JPAExpressions
                    .select(qPlanLike.count())
                    .from(qPlanLike)
                    .where(qPlanLike.plan.eq(qPlan))
            ))
            .from(qPlan)
            .where(qPlan.user.email.eq(email))
            .leftJoin(qPlan.user).fetchJoin()
            .leftJoin(qPlan.planLikes)
            .orderBy(qPlan.createdAt.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        JPAQuery<Long> count = queryFactory
            .select(qPlan.count())
            .from(qPlan)
            .where(qPlan.user.email.eq(email));

        return PageableExecutionUtils.getPage(plans, pageable, count::fetchOne);
    }

    @Override
    public Plan findPlan(Long id) {
        return queryFactory
            .selectFrom(qPlan)
            .leftJoin(qPlan.user).fetchJoin()
            .leftJoin(qPlan.planItems, qPlanItem).fetchJoin()
            .leftJoin(qPlanItem.place).fetchJoin()
            .where(qPlan.id.eq(id))
            .fetchOne();
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
