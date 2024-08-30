package team.seventhmile.tripforp.domain.plan.service;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanItemRequest;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanRequest;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanResponse;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanItemDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanListItemDto;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Place;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.PlanItem;
import team.seventhmile.tripforp.domain.plan.repository.PlaceRepository;
import team.seventhmile.tripforp.domain.plan.repository.PlanGetRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanService {

    private final PlanGetRepository planRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public CreatePlanResponse create(CreatePlanRequest request) {

        Plan plan = Plan.builder()
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .title(request.getTitle())
            .area(Area.valueOf(request.getArea()))
            .build();

        for (PlanItemDto planItemData : request.getPlanItems()) {
            //장소 가져오기, 없을 경우 장소 등록
            Place place = placeRepository.findByAddressNameAndPlaceName(
                    planItemData.getPlace().getAddressName(),
                    planItemData.getPlace().getPlaceName())
                .orElseGet(() -> {
                    Place newPlace = Place.builder()
                        .addressName(planItemData.getPlace().getAddressName())
                        .categoryName(planItemData.getPlace().getCategoryName())
                        .placeName(planItemData.getPlace().getPlaceName())
                        .placeUrl(planItemData.getPlace().getPlaceUrl())
                        .build();
                    return placeRepository.save(newPlace);
                });
            PlanItem planItem = PlanItem.builder()
                .place(place)
                .sequence(planItemData.getSequence())
                .tripDate(planItemData.getTripDate())
                .memo(planItemData.getMemo())
                .build();
            plan.addPlanItem(planItem);
        }

        planRepository.save(plan);

        return CreatePlanResponse.builder()
            .id(plan.getId())
            .build();
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
