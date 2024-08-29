package team.seventhmile.tripforp.domain.plan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanItemRequest;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanRequest;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanResponse;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanListItemDto;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanItemRequest;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanRequest;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanResponse;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.repository.PlanGetRepository;

import java.util.List;
import java.util.stream.Collectors;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanService {

    private final PlanGetRepository planRepository;
    private final PlaceService placeService;
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

    public List<PlanGetDto> getPlansByArea(Area area) {
        List<Plan> plans = planRepository.findByArea(area);
        return plans.stream().map(plan -> {
            List<PlanListItemDto> planItemDtos = plan.getPlanItems().stream()
                .map(planItem -> new PlanListItemDto(planItem.getSequence(),
                    planItem.getPlace().getPlaceName()))
                .collect(Collectors.toList());
            return new PlanGetDto(plan.getTitle(), plan.getViews(), planItemDtos);
        }).collect(Collectors.toList());
    }
}
