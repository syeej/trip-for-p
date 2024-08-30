package team.seventhmile.tripforp.domain.free_post.controller;

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
import team.seventhmile.tripforp.domain.free_post.dto.FreePostDto;
import team.seventhmile.tripforp.domain.free_post.service.FreePostService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(value = FreePostController.class)
public class FreePostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FreePostService freePostService;

    FreePostDto freePostDto;

    @BeforeEach
    void setUp() {
        freePostDto = new FreePostDto();
    }

    @Test
    @DisplayName("자유 게시글 생성")
    void createFreePost() throws Exception {

        when(freePostService.createFreePost(any(FreePostDto.class))).thenReturn(freePostDto);

        mockMvc.perform(post("/api/free-posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));

        verify(freePostService).createFreePost(any(FreePostDto.class));
    }

    @Test
    @DisplayName("자유 게시글 수정")
    void updateFreePost() throws Exception {

        when(freePostService.updateFreePost(anyLong(), any(FreePostDto.class))).thenReturn(freePostDto);

        mockMvc.perform(put("/api/free-posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(freePostService).updateFreePost(eq(1L), any(FreePostDto.class));
    }

    @Test
    @DisplayName("자유 게시글 삭제")
    void deleteFreePost() throws Exception {
        mockMvc.perform(delete("/api/free-posts/1"))
                .andExpect(status().isOk());

        verify(freePostService).deleteFreePost(1L);
    }

    @Test
    @DisplayName("자유 게시글 상세 조회")
    void getFreePost() throws Exception {
        when(freePostService.getFreePostDetail(anyLong())).thenReturn(freePostDto);

        mockMvc.perform(get("/api/free-posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));

        verify(freePostService).getFreePostDetail(1L);
    }

    @Test
    @DisplayName("검색 없이 자유 게시글 목록 조회")
    void getFreePostsNotKeyword() throws Exception {
        List<FreePostDto> posts = Arrays.asList(new FreePostDto(), new FreePostDto());
        Page<FreePostDto> page = new PageImpl<>(posts, PageRequest.of(0, 10), posts.size());

        when(freePostService.getAllFreePost(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/free-posts")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10));

        verify(freePostService).getAllFreePost(PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("키워드로 자유 게시글 검색")
    void getFreePostsByKeyword() throws Exception {
        List<FreePostDto> posts = Arrays.asList(new FreePostDto(), new FreePostDto());
        Page<FreePostDto> page = new PageImpl<>(posts, PageRequest.of(0, 10), posts.size());

        when(freePostService.getFreePostSearch(eq("test"), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/free-posts")
                        .param("page", "0")
                        .param("size", "10")
                        .param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10));

        verify(freePostService).getFreePostSearch("test", PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("빈 키워드로 자유 게시글 목록 조회")
    void getFreePostsByEmptyKeyword() throws Exception {
        List<FreePostDto> posts = Arrays.asList(new FreePostDto(), new FreePostDto());
        Page<FreePostDto> page = new PageImpl<>(posts, PageRequest.of(0, 10), posts.size());

        when(freePostService.getAllFreePost(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/free-posts")
                        .param("page", "0")
                        .param("size", "10")
                        .param("keyword", "  "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10));

        verify(freePostService).getAllFreePost(PageRequest.of(0, 10));
    }


}

