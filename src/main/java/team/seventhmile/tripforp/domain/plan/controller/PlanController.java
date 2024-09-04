package team.seventhmile.tripforp.domain.plan.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import team.seventhmile.tripforp.domain.plan.dto.GetPlanResponse;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanRequest;
import team.seventhmile.tripforp.domain.plan.dto.UpdatePlanResponse;
import team.seventhmile.tripforp.domain.plan.service.PlanService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;

    /**
     * 여행 코스를 등록합니다.
     *
     * @param request 여행 코스 생성에 필요한 세부 정보가 포함된 요청 본문
     * @param user 여행 코스를 생성하는 인증된 사용자
     * @return 생성된 여행 코스의 id를 포함한 ResponseEntity
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<CreatePlanResponse> createPlan(
        @Valid @RequestBody CreatePlanRequest request,
        @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(planService.createPlan(request, user));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<UpdatePlanResponse> updatePlan(
        @PathVariable("id") Long id,
        @RequestBody UpdatePlanRequest request,
        @AuthenticationPrincipal UserDetails user
    ) {
        return ResponseEntity.ok(planService.updatePlan(id, request, user));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(
        @PathVariable("id") Long id,
        @AuthenticationPrincipal UserDetails user
    ) {
        planService.deletePlan(id, user);
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    @GetMapping
    public ResponseEntity<Page<GetPlanListResponse>> getPlanList(
        @RequestParam(value = "area", required = false) String area,
        Pageable pageable
    ) {
        return ResponseEntity.ok(planService.getPlanList(area, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetPlanResponse> getPlanDetail(
        @PathVariable Long id
    ) {
        GetPlanResponse planDto = planService.getPlanById(id);
        return ResponseEntity.ok(planDto);
    }

    @GetMapping("/me")
    public ResponseEntity<Page<GetPlanListResponse>> getMyPlanList(
        @AuthenticationPrincipal UserDetails user,
        Pageable pageable
    ) {
        return ResponseEntity.ok(planService.getMyPlanList(user, pageable));
    }
}
