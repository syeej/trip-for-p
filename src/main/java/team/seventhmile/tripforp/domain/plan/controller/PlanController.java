package team.seventhmile.tripforp.domain.plan.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
import team.seventhmile.tripforp.domain.plan.dto.GetPlanListResponse;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDetailDto;
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
        @RequestBody CreatePlanRequest request,
        @AuthenticationPrincipal UserDetails authenticatedPrincipal) {
        return planService.createPlan(request, authenticatedPrincipal);
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

//    @GetMapping
//    public List<PlanGetDto> getPlansByArea(@RequestParam("area") String area) {
//        return planService.getPlansByArea(area);
//    }

    @GetMapping
    public Page<GetPlanListResponse> getPlansByArea(
        @RequestParam(value = "area", required = false) String area, Pageable pageable) {
        return planService.getPlanList(area, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlanDetail(@PathVariable Long id) {
        PlanGetDetailDto planDto = planService.getPlanById(id);
        return ResponseEntity.ok(planDto);
    }
}
