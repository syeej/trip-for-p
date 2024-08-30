package team.seventhmile.tripforp.domain.free_post.service;

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
import team.seventhmile.tripforp.domain.free_comment.entity.FreeComment;
import team.seventhmile.tripforp.domain.free_post.dto.FreePostDto;
import team.seventhmile.tripforp.domain.free_post.entity.FreePost;
import team.seventhmile.tripforp.domain.free_post.repository.FreePostRepository;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.review_post.dto.ReviewPostDto;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;
import team.seventhmile.tripforp.domain.review_post.repository.ReviewPostRepository;
import team.seventhmile.tripforp.domain.review_post.service.ReviewPostService;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FreePostServiceTest {

    @Mock
    private FreePostRepository freePostRepository;

    @InjectMocks
    private FreePostService freePostService;

    private FreePost freePost;
    private User user;
    private FreePostDto freePostDto;
    private List<FreeComment> comments;
    private List<FreePost> freePostList = new ArrayList<>();

    @BeforeEach
    void setUp() {

        user = new User(
                1L,
                "email",
                "1234",
                "nickname",
                false,
                Role.USER);

        comments = new ArrayList<>();
        comments.add(new FreeComment());

        freePost = FreePost.builder()
                .id(1L)
                .user(user)
                .content("자유 게시글 제목")
                .views(100)
                .comments(comments)
                .build();

        freePostDto = FreePostDto.builder()
                .id(1L)
                .userId(user.getId())
                .content("자유 게시글 내용")
                .views(100)
                .build();

        // 페이징 테스트를 위한 ReviewPostList 생성
        for (int i = 1; i <= 20; i++) {
            FreePost freePost = new FreePost(
                    null,
                    user,
                    "자유 게시글 내용 " + i,
                    100 + i,
                    comments);
            freePostList.add(freePost);
        }


    }

    @Test
    @DisplayName("자유 게시글 생성")
    void createFreePost() {
        // given
        when(freePostRepository.save(any(FreePost.class))).thenReturn(freePost);

        // when
        FreePostDto createdFreePost = freePostService.createFreePost(freePostDto);

        // then
        assertThat(createdFreePost).isNotNull();
        verify(freePostRepository, times(1)).save(any(FreePost.class));
    }

    @Test
    @DisplayName("자유 게시글 수정")
    void updateFreePost() {
        // given
        FreePost updatedPost = FreePost.builder()
                .id(1L)
                .user(user)
                .content("자유 게시글 내용")
                .views(100)
                .comments(comments)
                .build();

        FreePostDto updatedDto = FreePostDto.builder()
                .id(1L)
                .userId(user.getId())
                .content("수정된 자유 게시글 내용")
                .views(100)
                .comments(comments)
                .build();

        when(freePostRepository.findById(1L)).thenReturn(Optional.of(freePost));
        when(freePostRepository.save(any(FreePost.class))).thenReturn(updatedPost);

        // when
        FreePostDto updateResult = freePostService.updateFreePost(1L, updatedDto);

        // then
        assertThat(updateResult.getId()).isEqualTo(1L);
        assertThat(updateResult.getContent()).isEqualTo("수정된 자유 게시글 내용");
        verify(freePostRepository, times(1)).save(any(FreePost.class));
    }

    @Test
    @DisplayName("자유 게시글 상세 조회")
    void getFreePostDetail() {
        // given
        when(freePostRepository.findById(1L)).thenReturn(Optional.of(freePost));

        // when
        FreePostDto freePostDetail = freePostService.getFreePostDetail(1L);

        // then
        assertThat(freePostDetail).isNotNull();
        verify(freePostRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("자유 게시글 삭제")
    void deleteFreePost() {
        // given
        when(freePostRepository.findById(1L)).thenReturn(Optional.of(freePost));
        doNothing().when(freePostRepository).delete(freePost);

        // when
        freePostService.deleteFreePost(1L);

        // then
        verify(freePostRepository, times(1)).findById(1L);
        verify(freePostRepository, times(1)).delete(freePost);
    }

    @Test
    @DisplayName("자유 게시글 pagination")
    void getAllFreePost() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<FreePost> pageContent = freePostList.subList(0, pageable.getPageSize());
        Page<FreePost> freePostPage = new PageImpl<>(pageContent, pageable, freePostList.size());

        when(freePostRepository.getFreePosts(pageable)).thenReturn(freePostPage);

        // when
        Page<FreePostDto> result = freePostService.getAllFreePost(pageable);

        // then
        assertThat(result.getContent()).hasSize(pageable.getPageSize());
        assertThat(result.getTotalElements()).isEqualTo(20);
        assertThat(result.getNumber()).isEqualTo(pageable.getPageNumber());
        verify(freePostRepository, times(1)).getFreePosts(pageable);
    }

    @Test
    @DisplayName("자유 게시글 검색(제목, 내용) + pagination")
    void searchFreePost() {

        // given
        // 제목, 내용에 20이 포함된 데이터 추출
        List<FreePost> searchedList = new ArrayList<>();
        for (FreePost freePost : freePostList) {
            if (freePost.getContent().contains("20")) {
                searchedList.add(freePost);
            }
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<FreePost> freePostPage = new PageImpl<>(searchedList, pageable, searchedList.size());

        when(freePostRepository.getFreePostKeywordContaining(anyString(), any(Pageable.class))).thenReturn(freePostPage);

        // when
        Page<FreePostDto> result = freePostService.getFreePostSearch("20", pageable);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(searchedList.size());


    }

}
