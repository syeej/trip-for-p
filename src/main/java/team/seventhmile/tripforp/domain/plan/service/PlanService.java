package team.seventhmile.tripforp.domain.plan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanItemRequest;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanRequest;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanResponse;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDetailDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetItemDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanItemDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanLikeDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanListItemDto;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanItemRequest;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanRequest;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanResponse;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Plan;

import java.util.List;
import java.util.stream.Collectors;
import team.seventhmile.tripforp.domain.plan.repository.PlanRepository;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanItemService planItemService;

    @Transactional
    public CreatePlanResponse createPlan(CreatePlanRequest request) {

        Plan plan = Plan.builder()
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .title(request.getTitle())
            .area(request.getArea())
            .build();

        for (CreatePlanItemRequest planItemRequest : request.getPlanItems()) {
            //장소 가져오기, 없을 경우 장소 등록
            planItemService.createPlanItem(plan, planItemRequest);
        }
        return new CreatePlanResponse(planRepository.save(plan).getId());
    }

    @Transactional
    public UpdatePlanResponse updatePlan(Long id, UpdatePlanRequest request) {
        Plan plan = planRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Plan.class, id));

        plan.updatePlan(request);

        for (UpdatePlanItemRequest planItemRequest : request.getPlanItems()) {
            planItemService.updateOrCreatePlanItem(plan, planItemRequest);
        }

        return new UpdatePlanResponse(id);
    }

    @Transactional
    public void deletePlan(Long id) {
        Plan plan = planRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Plan.class, id));

        planRepository.delete(plan);
    }

    public List<PlanGetDto> getPlansByArea(String areaName) {
        Area area = Area.fromName(areaName);
        List<Plan> plans = planRepository.findByArea(area);
        return plans.stream().map(plan -> {
            List<PlanListItemDto> planItemDtos = plan.getPlanItems().stream()
                .map(planItem -> new PlanListItemDto(planItem.getSequence(),
                    planItem.getPlace().getPlaceName()))
                .collect(Collectors.toList());
            return new PlanGetDto(plan.getTitle(), plan.getViews(), planItemDtos);
        }).collect(Collectors.toList());
    }

    public PlanGetDetailDto getPlanById(Long planId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new IllegalArgumentException("Plan not found with id: " + planId));

        // PlanItems를 PlanItemDto로 변환
        List<PlanGetItemDto> planItemDtos = plan.getPlanItems().stream()
            .map(PlanGetItemDto::new)
            .collect(Collectors.toList());

        // PlanLikes를 PlanLikeDto로 변환
        List<PlanLikeDto> planLikeDtos = plan.getPlanLikes().stream()
            .map(like -> new PlanLikeDto(
                like.getId(),
                like.getUser(),like.getPlan()))
            .collect(Collectors.toList());

        return new PlanGetDetailDto(
            plan.getId(),
            plan.getTitle(),
            plan.getStartDate(),
            plan.getEndDate(),
            plan.getArea(),
            plan.getViews(),
            planItemDtos,
            planLikeDtos
        );
    }
}
