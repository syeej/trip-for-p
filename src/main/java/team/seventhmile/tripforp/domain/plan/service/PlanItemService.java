package team.seventhmile.tripforp.domain.plan.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanItemRequest;
import team.seventhmile.tripforp.domain.plan.dto.GetPlaceCountResponse;
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
        Place place = placeService.findOrCreatePlace(request.getPlace());
        PlanItem planItem = PlanItem.builder()
            .place(place)
            .sequence(request.getSequence())
            .tripDate(request.getTripDate())
            .memo(request.getMemo())
            .build();
        plan.addPlanItem(planItem);
    }

    @Transactional
    public void managePlanItems(Plan plan, List<UpdatePlanItemRequest> requests) {

        requests.stream()
            .filter(req -> req.getAction().equals("delete"))
            .forEach(req -> {
                PlanItem planItem = planItemRepository.findById(req.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(PlanItem.class, req.getId()));
                plan.removePlanItem(planItem);
            });
        planItemRepository.flush();

        requests.stream()
            .filter(req -> req.getAction().equals("update"))
            .forEach(req -> {
                Place place = placeService.findOrCreatePlace(req.getPlace());
                PlanItem planItem = planItemRepository.findById(req.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(PlanItem.class, req.getId()));
                planItem.updatePlanItem(place, req);
            });
        planItemRepository.flush();

        requests.stream()
            .filter(req -> req.getAction().equals("create"))
            .forEach(req -> {
                Place place = placeService.findOrCreatePlace(req.getPlace());
                PlanItem savePlanItem = PlanItem.builder()
                    .place(place)
                    .sequence(req.getSequence())
                    .tripDate(req.getTripDate())
                    .memo(req.getMemo())
                    .build();
                plan.addPlanItem(savePlanItem);
            });
    }

    public List<GetPlaceCountResponse> getPlaceCount() {
        return planItemRepository.getPlaceCount(PageRequest.of(0, 6));
    }

}
