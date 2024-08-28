package team.seventhmile.tripforp.domain.plan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDto;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.service.PlanGetService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlanGetController {

    private final PlanGetService planService;

    @GetMapping("/api/plans")
    public List<PlanGetDto> getPlansByArea(@RequestParam("area") Area area) {
        return planService.getPlansByArea(area);
    }

}
