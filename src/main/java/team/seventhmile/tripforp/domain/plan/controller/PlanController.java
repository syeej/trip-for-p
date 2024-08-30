package team.seventhmile.tripforp.domain.plan.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanRequest;
import team.seventhmile.tripforp.domain.plan.dto.CreatePlanResponse;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDto;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanRequest;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanResponse;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.service.PlanService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;

    @PostMapping
    public CreatePlanResponse createPlan(
        @RequestBody CreatePlanRequest request
    ) {
        return planService.createPlan(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdatePlanResponse> updatePlan(
        @PathVariable("id") Long id,
        @RequestBody UpdatePlanRequest request
    ) {
        return ResponseEntity.ok(planService.updatePlan(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(
        @PathVariable("id") Long id
    ) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<PlanGetDto> getPlansByArea(@RequestParam("area") Area area) {
        return planService.getPlansByArea(area);
    }

}
