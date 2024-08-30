package team.seventhmile.tripforp.domain.plan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanItemRequest;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanItemRequest;
import team.seventhmile.tripforp.domain.plan.entity.Place;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.PlanItem;
import team.seventhmile.tripforp.domain.plan.repository.PlanItemRepository;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanItemService {

    private final PlaceService placeService;
    private final PlanItemRepository planItemRepository;

    @Transactional
    public void createPlanItem(Plan plan, CreatePlanItemRequest request) {
        Place place = placeService.FindOrCreatePlace(request.getPlace());
        PlanItem planItem = PlanItem.builder()
            .place(place)
            .sequence(request.getSequence())
            .tripDate(request.getTripDate())
            .memo(request.getMemo())
            .build();
        plan.addPlanItem(planItem);
    }

    @Transactional
    public void updateOrCreatePlanItem(Plan plan, UpdatePlanItemRequest request) {
        Place place = placeService.FindOrCreatePlace(request.getPlace());

        if (request.getId() != null) {
            PlanItem planItem = planItemRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException(PlanItem.class, request.getId()));
            if (request.getAction().equals("update")) {
                planItem.updatePlanItem(place, request);
            } else if (request.getAction().equals("delete")) {
                plan.removePlanItem(planItem);
                planItemRepository.flush();
            }

        } else if (request.getAction().equals("create")) {
            PlanItem planItem = PlanItem.builder()
                .place(place)
                .sequence(request.getSequence())
                .tripDate(request.getTripDate())
                .memo(request.getMemo())
                .build();
            plan.addPlanItem(planItem);
        }
    }

}
