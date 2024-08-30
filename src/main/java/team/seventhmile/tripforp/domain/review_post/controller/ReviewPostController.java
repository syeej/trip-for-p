package team.seventhmile.tripforp.domain.review_post.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import team.seventhmile.tripforp.domain.review_post.dto.ReviewPostDto;
import team.seventhmile.tripforp.domain.review_post.service.ReviewPostService;

@RestController
@RequestMapping("/api/review-posts")
public class ReviewPostController {

    private final ReviewPostService reviewPostService;

    @Autowired
    public ReviewPostController(ReviewPostService reviewPostService) {
        this.reviewPostService = reviewPostService;
    }

    @PostMapping
    public ReviewPostDto createReviewPost(@RequestBody ReviewPostDto reviewPostDto) {
        return reviewPostService.createReviewPost(reviewPostDto);
    }

    @PutMapping("/{id}")
    public ReviewPostDto updateReviewPost(@PathVariable("id") Long id, @RequestBody ReviewPostDto reviewPostDto) {
        return reviewPostService.updateReviewPost(id, reviewPostDto);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewPost(@PathVariable("id") Long id) {
        reviewPostService.deleteReviewPost(id);
    }

    // 목록 조회, 검색 목록 조회 통합(검색 키워드 여부)
    @GetMapping
    public Page<ReviewPostDto> getReviewPosts(@RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size,
                                              @RequestParam(value = "keyword", required = false) String keyword) {


        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ReviewPostDto> reviewPostDto;

        if (keyword != null && !keyword.trim().isEmpty()) {
            reviewPostDto = reviewPostService.getReviewPostSearch(keyword, pageRequest);
        } else {
            reviewPostDto = reviewPostService.getAllReviewPost(pageRequest);
        }

        return reviewPostDto;
    }

    @GetMapping("/{id}")
    public ReviewPostDto getReviewPostDetail(@PathVariable("id") Long id) {
        return reviewPostService.getReviewPostDetail(id);
    }


}
