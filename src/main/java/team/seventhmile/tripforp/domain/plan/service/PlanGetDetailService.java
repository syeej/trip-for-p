package team.seventhmile.tripforp.domain.plan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.seventhmile.tripforp.domain.plan.dto.*;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.repository.PlanGetDetailRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanGetDetailService {

    private final PlanGetDetailRepository planRepository;

    public PlanGetDetailDto getPlanById(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found with id: " + planId));

        // PlanItems를 PlanItemDto로 변환
        List<PlanGetItemDto> planItemDtos = plan.getPlanItems().stream()
                .map(item -> new PlanGetItemDto(
                        item.getId(),
                        new PlaceGetDto(
                                item.getPlace().getId(),
                                item.getPlace().getTitle(),
                                item.getPlace().getAddress(),
                                item.getPlace().getMapX(),
                                item.getPlace().getMapY()
                        ),
                        item.getTripDate(),
                        item.getMemo(),
                        item.getSequence()))
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
