package team.seventhmile.tripforp.domain.plan.controller;

import org.springframework.web.bind.annotation.*;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.entity.PlanLike;
import team.seventhmile.tripforp.domain.plan.service.PlanLikeService;
import team.seventhmile.tripforp.domain.user.entity.User;

@RestController
@RequestMapping("/api/plan-likes")
public class PlanLikeController {

    private final PlanLikeService planLikeService;

    public PlanLikeController(PlanLikeService planLikeService) {
        this.planLikeService = planLikeService;
    }

    // 사용자로부터 여행 코스를 좋아요하는 요청 처리
    @PostMapping
    public PlanLike likePlan(@RequestBody User user, @RequestBody Plan plan) {
        return planLikeService.likePlan(user, plan);
    }

    // 사용자로부터 좋아요를 취소하는 요청 처리
    @DeleteMapping("/{likeId}")
    public void unlikePlan(@PathVariable Long likeId) {
        planLikeService.unlikePlan(likeId);
    }
}
