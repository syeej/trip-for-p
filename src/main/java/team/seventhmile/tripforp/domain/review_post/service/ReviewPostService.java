package team.seventhmile.tripforp.domain.review_post.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.plan.repository.PlanRepository;
import team.seventhmile.tripforp.domain.review_post.dto.ReviewPostDto;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;
import team.seventhmile.tripforp.domain.review_post.repository.ReviewPostRepository;

@Service
public class ReviewPostService {

    private final ReviewPostRepository reviewPostRepository;
    private final PlanRepository planRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    public ReviewPostService(ReviewPostRepository reviewPostRepository,
                             JPAQueryFactory jpaQueryFactory,
                             PlanRepository planRepository) {
        this.reviewPostRepository = reviewPostRepository;
        this.jpaQueryFactory = jpaQueryFactory;
        this.planRepository = planRepository;
    }

    // 리뷰 게시글 생성
    @Transactional
    public ReviewPostDto createReviewPost(ReviewPostDto reviewPostDto) {

        ReviewPost reviewPost = reviewPostDto.toEntity();
        reviewPostRepository.save(reviewPost);

        return ReviewPostDto.fromEntity(reviewPost);

    }

    // 리뷰 게시글 수정
    @Transactional
    public ReviewPostDto updateReviewPost(Long id,
                                          ReviewPostDto reviewPostDto) {

        ReviewPost reviewPost = reviewPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("데이터를 찾을 수 없습니다."));

        // 상황에 따라 Service에 의존할지, Repository에 의존할지 결정
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("데이터가 없습니다."));

        reviewPost.update(
                plan,
                reviewPostDto.getTitle(),
                reviewPostDto.getContent(),
                reviewPostDto.getViews()
        );

        reviewPostRepository.save(reviewPost);

        return ReviewPostDto.fromEntity(reviewPost);

    }

    // 리뷰 게시글 삭제
    @Transactional
    public void deleteReviewPost(Long id) {

        ReviewPost reviewPost = reviewPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("데이터를 찾을 수 없습니다."));
        reviewPostRepository.delete(reviewPost);

    }

    // 리뷰 게시글 목록 조회
    @Transactional(readOnly = true)
    public Page<ReviewPostDto> getAllReviewPost(Pageable pageable) {

        return reviewPostRepository.getReviewPosts(pageable)
                .map(ReviewPostDto::fromEntity);

    }

    // 리뷰 게시글 상세 조회
    @Transactional(readOnly = true)
    public ReviewPostDto getReviewPostDetail(Long id) {

        return reviewPostRepository.findById(id)
                .map(ReviewPostDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("데이터를 찾을 수 없습니다."));

    }

    // 리뷰 게시글 검색(제목, 내용) 조회
    @Transactional(readOnly = true)
    public Page<ReviewPostDto> getReviewPostSearch(String keyword, Pageable pageable) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty(pageable);
        }

        Page<ReviewPost> reviewPosts = reviewPostRepository.getReviewPostKeywordContaining(keyword.trim(), pageable);
        return reviewPosts.map(ReviewPostDto::fromEntity);

    }

    // ReviewPost 엔티티 조회
    @Transactional(readOnly = true)
    public ReviewPost getReviewPostEntity(Long id) {
        return reviewPostRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("데이터를 찾을 수 없습니다."));
    }


}
