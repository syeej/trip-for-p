package team.seventhmile.tripforp.domain.plan.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

   //IllegalArgumentException을 처리할 수 있는 예외 핸들러
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"" + ex.getMessage() + "\"}");
    }
}
