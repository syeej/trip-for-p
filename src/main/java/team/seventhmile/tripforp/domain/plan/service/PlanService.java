package team.seventhmile.tripforp.domain.plan.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanItemRequest;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanRequest;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanResponse;
import team.seventhmile.tripforp.domain.plan.dto.GetPlanListResponse;
import team.seventhmile.tripforp.domain.plan.dto.GetPlanResponse;
import team.seventhmile.tripforp.domain.plan.dto.GetPopularPlanResponse;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanListItemDto;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanItemRequest;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanRequest;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanResponse;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.repository.PlanLikeRepository;
import team.seventhmile.tripforp.domain.plan.repository.PlanRepository;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;
import team.seventhmile.tripforp.global.exception.UnauthorizedAccessException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanItemService planItemService;
    private final PlanLikeRepository planLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreatePlanResponse createPlan(CreatePlanRequest request, UserDetails user) {

        User findUser = userRepository.findByEmail(user.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException(User.class));

        Plan plan = Plan.builder()
            .user(findUser)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .title(request.getTitle())
            .area(request.getArea())
            .build();
        planRepository.save(plan);

        for (CreatePlanItemRequest planItemRequest : request.getPlanItems()) {
            planItemService.createPlanItem(plan, planItemRequest);
        }
        return new CreatePlanResponse(plan.getId());
    }

    @Transactional
    public UpdatePlanResponse updatePlan(Long id, UpdatePlanRequest request, UserDetails user) {

        Plan plan = planRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Plan.class, id));

        checkUpdateAuthorization(user, plan);

        plan.updatePlan(request);

        for (UpdatePlanItemRequest planItemRequest : request.getPlanItems()) {
            planItemService.updateOrCreatePlanItem(plan, planItemRequest);
        }

        return new UpdatePlanResponse(id);
    }

    @Transactional
    public void deletePlan(Long id, UserDetails user) {
        Plan plan = planRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Plan.class, id));

        checkDeleteAuthorization(user, plan);

        planRepository.delete(plan);
    }

    @Transactional
    public GetPlanResponse getPlanById(Long planId) {

        Plan plan = planRepository.findPlan(planId);
        int likeCount = planLikeRepository.countByPlanId(planId);
        plan.increaseViews();

        return new GetPlanResponse(plan, likeCount);
    }

    public Page<GetPlanListResponse> getPlanList(String area, Pageable pageable) {
        return planRepository.getPlans(area, pageable);
    }

    public Page<GetPlanListResponse> getMyPlanList(UserDetails user, Pageable pageable) {
        return planRepository.getMyPlans(user.getUsername(), pageable);
    }

    /**
     * 수정, 삭제 시 작성자 본인이 맞는지 검증
     */
    private void checkUpdateAuthorization(UserDetails user, Plan plan) {
        if (!user.getUsername().equals(plan.getUser().getEmail())) {
            System.out.println(user.getAuthorities());
            throw new UnauthorizedAccessException(Plan.class);
        }
    }

    private void checkDeleteAuthorization(UserDetails user, Plan plan) {
        if (!user.getUsername().equals(plan.getUser().getEmail()) && !user.getAuthorities().contains("ROLE_ADMIN")) {
            throw new UnauthorizedAccessException(Plan.class);
        }
    }

    public List<PlanGetDto> getPlansByArea(String areaName) {
        Area area = Area.fromName(areaName);

        List<Plan> plans = planRepository.findByArea(area);

        return plans.stream().map(plan -> {

            List<PlanListItemDto> planItemDtos = plan.getPlanItems().stream()
                .map(planItem -> new PlanListItemDto(
                    planItem.getSequence(),
                    planItem.getPlace().getPlaceName()))
                .collect(Collectors.toList());

            return new PlanGetDto(
                plan.getUser(),
                plan.getTitle(),
                plan.getViews(),
                planItemDtos
            );
        }).collect(Collectors.toList());
    }

    public List<GetPopularPlanResponse> getPopularPlanList() {
        return planRepository.findPopularPlans(PageRequest.of(0, 6));
    }
}
