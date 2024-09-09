package team.seventhmile.tripforp.domain.plan.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team.seventhmile.tripforp.domain.plan.dto.GetPlanListResponse;
import team.seventhmile.tripforp.domain.plan.dto.PlanLikeRequestDto;
import team.seventhmile.tripforp.domain.plan.dto.PlanLikeResponseDto;
import team.seventhmile.tripforp.domain.plan.service.PlanLikeService;

@RestController
@RequestMapping("/api/plan-likes")
public class PlanLikeController {

  private final PlanLikeService planLikeService;

  public PlanLikeController(PlanLikeService planLikeService) {
    this.planLikeService = planLikeService;
  }

  // 좋아요 또는 좋아요 취소를 처리하는 엔드포인트
  @PostMapping
  public ResponseEntity<PlanLikeResponseDto> toggleLikePlan(
          @AuthenticationPrincipal UserDetails userDetails,
          @RequestBody PlanLikeRequestDto requestDto) {

    // userDetails에서 이메일을 추출하여 서비스에 전달
    PlanLikeResponseDto responseDto = planLikeService.toggleLikePlan(userDetails.getUsername(), requestDto.getPlanId());
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
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
