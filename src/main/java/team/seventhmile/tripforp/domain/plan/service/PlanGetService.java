package team.seventhmile.tripforp.domain.plan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanListItemDto;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.repository.PlanGetRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanGetService {
    private final PlanGetRepository planRepository;

    public List<PlanGetDto> getPlansByArea(Area area) {
        List<Plan> plans = planRepository.findByArea(area);
        return plans.stream().map(plan -> {
            List<PlanListItemDto> planItemDtos = plan.getPlanItems().stream()
                    .map(planItem -> new PlanListItemDto(planItem.getSequence(), planItem.getPlace().getTitle()))
                    .collect(Collectors.toList());
            return new PlanGetDto(plan.getTitle(), plan.getViews(), planItemDtos);
        }).collect(Collectors.toList());
    }
}
