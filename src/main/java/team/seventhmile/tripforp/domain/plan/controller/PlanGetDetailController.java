package team.seventhmile.tripforp.domain.plan.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.seventhmile.tripforp.domain.plan.dto.PlanGetDetailDto;
import team.seventhmile.tripforp.domain.plan.service.PlanGetDetailService;

@RestController
@RequestMapping("/api/plans")
public class PlanGetDetailController {

    private final PlanGetDetailService planService;

    public PlanGetDetailController(PlanGetDetailService planGetDetailService) {
        this.planService = planGetDetailService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlan(@PathVariable Long id) {
        try {
            PlanGetDetailDto planDto = planService.getPlanById(id);
            return ResponseEntity.ok(planDto);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    private static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}