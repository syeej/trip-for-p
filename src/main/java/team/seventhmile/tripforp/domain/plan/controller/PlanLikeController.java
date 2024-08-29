package team.seventhmile.tripforp.domain.plan.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.seventhmile.tripforp.domain.plan.dto.PlanLikeRequestDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanLikeResponseDto;
import team.seventhmile.tripforp.domain.plan.entity.PlanLike;
import team.seventhmile.tripforp.domain.plan.service.PlanLikeService;

import static team.seventhmile.tripforp.domain.plan.entity.QPlanLike.planLike;


@RestController
@RequestMapping("/api/plan-likes")
public class PlanLikeController {

    private final PlanLikeService planLikeService;

    public PlanLikeController(PlanLikeService planLikeService) {
        this.planLikeService = planLikeService;
    }

    // 사용자로부터 여행 코스를 좋아요하는 요청 처리
    @PostMapping
    public ResponseEntity<PlanLikeResponseDto> likePlan(@RequestBody PlanLikeRequestDto requestDto) {
        PlanLikeResponseDto responseDto = planLikeService.likePlan(requestDto.getUser(), requestDto.getPlan());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // 사용자로부터 좋아요를 취소하는 요청 처리
    @DeleteMapping("/{likeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlikePlan(@PathVariable Long likeId) {
        planLikeService.unlikePlan(likeId);
    }
}
