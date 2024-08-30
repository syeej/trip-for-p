package team.seventhmile.tripforp.domain.review_post.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import team.seventhmile.tripforp.domain.review_post.dto.ReviewPostDto;
import team.seventhmile.tripforp.domain.review_post.service.ReviewPostService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ReviewPostController.class)
public class ReviewPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewPostService reviewPostService;

    ReviewPostDto reviewPostDto;

    @BeforeEach
    void setUp() {
        reviewPostDto = new ReviewPostDto();
    }

    @Test
    @DisplayName("리뷰 게시글 생성")
    void createReviewPost() throws Exception {

        when(reviewPostService.createReviewPost(any(ReviewPostDto.class))).thenReturn(reviewPostDto);

        mockMvc.perform(post("/api/review-posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));

        verify(reviewPostService).createReviewPost(any(ReviewPostDto.class));
    }

    @Test
    @DisplayName("리뷰 게시글 수정")
    void updateReviewPost() throws Exception {

        when(reviewPostService.updateReviewPost(anyLong(), any(ReviewPostDto.class))).thenReturn(reviewPostDto);

        mockMvc.perform(put("/api/review-posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(reviewPostService).updateReviewPost(eq(1L), any(ReviewPostDto.class));
    }

    @Test
    @DisplayName("리뷰 게시글 삭제")
    void deleteReviewPost() throws Exception {
        mockMvc.perform(delete("/api/review-posts/1"))
                .andExpect(status().isOk());

        verify(reviewPostService).deleteReviewPost(1L);
    }

    @Test
    @DisplayName("리뷰 게시글 목록 조회")
    void getReviewPosts() {
    }

    @Test
    @DisplayName("리뷰 게시글 상세 조회")
    void getReviewPost() throws Exception {
        when(reviewPostService.getReviewPostDetail(anyLong())).thenReturn(reviewPostDto);

        mockMvc.perform(get("/api/review-posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));

        verify(reviewPostService).getReviewPostDetail(1L);
    }

    @Test
    @DisplayName("리뷰 게시글 검색 조회")
    void getReviewPostSearch() {
    }

    @Test
    @DisplayName("검색 없이 리뷰 게시글 목록 조회")
    void getReviewPostsNotKeyword() throws Exception {
        List<ReviewPostDto> posts = Arrays.asList(new ReviewPostDto(), new ReviewPostDto());
        Page<ReviewPostDto> page = new PageImpl<>(posts, PageRequest.of(0, 10), posts.size());

        when(reviewPostService.getAllReviewPost(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/review-posts")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10));

        verify(reviewPostService).getAllReviewPost(PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("키워드로 리뷰 게시글 검색")
    void getReviewPostsByKeyword() throws Exception {
        List<ReviewPostDto> posts = Arrays.asList(new ReviewPostDto(), new ReviewPostDto());
        Page<ReviewPostDto> page = new PageImpl<>(posts, PageRequest.of(0, 10), posts.size());

        when(reviewPostService.getReviewPostSearch(eq("test"), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/review-posts")
                        .param("page", "0")
                        .param("size", "10")
                        .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10));

        verify(reviewPostService).getReviewPostSearch("test", PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("빈 키워드로 리뷰 게시글 목록 조회")
    void getReviewPostsByEmptyKeyword() throws Exception {
        List<ReviewPostDto> posts = Arrays.asList(new ReviewPostDto(), new ReviewPostDto());
        Page<ReviewPostDto> page = new PageImpl<>(posts, PageRequest.of(0, 10), posts.size());

        when(reviewPostService.getAllReviewPost(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/review-posts")
                        .param("page", "0")
                        .param("size", "10")
                        .param("keyword", "  "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10));

        verify(reviewPostService).getAllReviewPost(PageRequest.of(0, 10));
    }


}
