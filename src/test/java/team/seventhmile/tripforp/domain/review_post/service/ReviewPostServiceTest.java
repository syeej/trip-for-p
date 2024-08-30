package team.seventhmile.tripforp.domain.review_post.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.review_post.dto.ReviewPostDto;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;
import team.seventhmile.tripforp.domain.review_post.repository.ReviewPostRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewPostServiceTest {

    @Mock
    private ReviewPostRepository reviewPostRepository;

    @InjectMocks
    private ReviewPostService reviewPostService;

    private Plan plan;
    private ReviewPost reviewPost;
    private ReviewPostDto reviewPostDto;
    private List<ReviewPost> reviewPostList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        plan = Plan.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .area("부산")
                .title("부산 여행")
                .build();

        reviewPost = ReviewPost.builder()
                .id(1L)
                .plan(plan)
                .title("리뷰 게시글 제목")
                .content("리뷰 게시글 제목")
                .views(100)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now().plusDays(2))
                .build();

        reviewPostDto = ReviewPostDto.builder()
                .id(1L)
                .planId(1L)
                .title("리뷰 게시글 제목")
                .content("리뷰 게시글 내용")
                .views(100)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

        // 페이징 테스트를 위한 ReviewPostList 생성
        for (int i = 1; i <= 20; i++) {
            ReviewPost reviewPost = new ReviewPost(
                    null,
                    plan,
                    "리뷰 게시글 제목 " + i,
                    "리뷰 게시글 내용 " + i,
                    100 + i,
                    LocalDate.now().minusDays(i),
                    LocalDate.now());
            reviewPostList.add(reviewPost);
        }


    }

    @Test
    @DisplayName("리뷰 게시글 생성")
    void createReviewPost() {
        // given
        when(reviewPostRepository.save(any(ReviewPost.class))).thenReturn(reviewPost);

        // when
        ReviewPostDto createdReviewPost = reviewPostService.createReviewPost(reviewPostDto);

        // then
        assertThat(createdReviewPost).isNotNull();
        assertThat(createdReviewPost.getTitle()).isEqualTo("리뷰 게시글 제목");
        verify(reviewPostRepository, times(1)).save(any(ReviewPost.class));
    }

    @Test
    @DisplayName("리뷰 게시글 수정")
    void updateReviewPost() {
        // given
        ReviewPost updatedPost = ReviewPost.builder()
                .id(1L)
                .plan(plan)
                .title("게시글 제목")
                .content("게시글 내용")
                .views(100)
                .createdAt(reviewPost.getCreatedAt())
                .updatedAt(LocalDate.now())
                .build();

        ReviewPostDto updatedDto = ReviewPostDto.builder()
                .id(1L)
                .planId(plan.getId())
                .title("수정된 게시글 제목")
                .content("수정된 게시글 내용")
                .views(100)
                .createdAt(reviewPost.getCreatedAt())
                .updatedAt(LocalDate.now())
                .build();

        when(reviewPostRepository.findById(1L)).thenReturn(Optional.of(reviewPost));
        when(reviewPostRepository.save(any(ReviewPost.class))).thenReturn(updatedPost);

        // when
        ReviewPostDto updateResult = reviewPostService.updateReviewPost(1L, updatedDto);

        // then
        assertThat(updateResult.getId()).isEqualTo(1L);
        assertThat(updateResult.getTitle()).isEqualTo("수정된 게시글 제목");
        assertThat(updateResult.getContent()).isEqualTo("수정된 게시글 내용");
        verify(reviewPostRepository, times(1)).save(any(ReviewPost.class));
    }

    @Test
    @DisplayName("리뷰 게시글 상세 조회")
    void getReviewPostDetail() {
        // given
        when(reviewPostRepository.findById(1L)).thenReturn(Optional.of(reviewPost));

        // when
        ReviewPostDto reviewPostDetail = reviewPostService.getReviewPostDetail(1L);

        // then
        assertThat(reviewPostDetail).isNotNull();
        verify(reviewPostRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("리뷰 게시글 삭제")
    void deleteReviewPost() {
        // given
        when(reviewPostRepository.findById(1L)).thenReturn(Optional.of(reviewPost));
        doNothing().when(reviewPostRepository).delete(reviewPost);

        // when
        reviewPostService.deleteReviewPost(1L);

        // then
        verify(reviewPostRepository, times(1)).findById(1L);
        verify(reviewPostRepository, times(1)).delete(reviewPost);
    }

    @Test
    @DisplayName("리뷰 게시글 pagination")
    void getAllReviewPost() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<ReviewPost> pageContent = reviewPostList.subList(0, pageable.getPageSize());
        Page<ReviewPost> reviewPostPage = new PageImpl<>(pageContent, pageable, reviewPostList.size());

        when(reviewPostRepository.getReviewPosts(pageable)).thenReturn(reviewPostPage);

        // when
        Page<ReviewPostDto> result = reviewPostService.getAllReviewPost(pageable);

        // then
        assertThat(result.getContent()).hasSize(pageable.getPageSize());
        assertThat(result.getTotalElements()).isEqualTo(20);
        assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
        verify(reviewPostRepository, times(1)).getReviewPosts(pageable);
    }

    @Test
    @DisplayName("리뷰 게시글 검색(제목, 내용) + pagination")
    void searchReviewPost() {

        // given
        // 제목, 내용에 20이 포함된 데이터 추출
        List<ReviewPost> searchedList = new ArrayList<>();
        for (ReviewPost reviewPost : reviewPostList) {
            if (reviewPost.getTitle().contains("20") || reviewPost.getContent().contains("20")) {
                searchedList.add(reviewPost);
            }
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<ReviewPost> reviewPostPage = new PageImpl<>(searchedList, pageable, searchedList.size());

        when(reviewPostRepository.getReviewPostKeywordContaining(anyString(), any(Pageable.class))).thenReturn(reviewPostPage);

        // when
        Page<ReviewPostDto> result = reviewPostService.getReviewPostSearch("20", pageable);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(searchedList.size());
        assertThat(result.getContent().get(0).getTitle()).contains("20");


    }

}
