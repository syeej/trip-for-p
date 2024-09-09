package team.seventhmile.tripforp.domain.plan.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import team.seventhmile.tripforp.domain.plan.dto.GetPlanListResponse;
import team.seventhmile.tripforp.domain.plan.dto.PlanLikeRequestDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanLikeResponseDto;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.service.PlanLikeService;

import java.util.List;

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
    PlanLikeResponseDto responseDto =
        planLikeService.likePlan(requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  // 사용자로부터 좋아요를 취소하는 요청 처리
  @DeleteMapping("/{likeId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unlikePlan(@PathVariable Long likeId) {
    planLikeService.unlikePlan(likeId);
  }

  // 좋아요 기준 상위 5개 여행코스 반환하는 엔드포인트
  @GetMapping("/top5")
  public ResponseEntity<List<Plan>> getTop5PlansByLikes() {
    List<Plan> topPlans = planLikeService.getTop5PlansByLikes();
    return new ResponseEntity<>(topPlans, HttpStatus.OK);
  }

  //[마이페이지]내가 좋아요한 글 목록 조회
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/me")
  public ResponseEntity<Page<GetPlanListResponse>> getMyFavPlanList(
          @AuthenticationPrincipal UserDetails user,
          Pageable pageable){
    return ResponseEntity.ok(planLikeService.getMyFavPlanList(user, pageable));
  }
}
