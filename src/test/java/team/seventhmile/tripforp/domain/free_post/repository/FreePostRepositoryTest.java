package team.seventhmile.tripforp.domain.free_post.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import team.seventhmile.tripforp.domain.free_comment.entity.FreeComment;
import team.seventhmile.tripforp.domain.free_post.entity.FreePost;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.global.config.QuerydslConfig;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application.yml")
@Import(QuerydslConfig.class)
public class FreePostRepositoryTest {

    @Autowired
    private FreePostRepository freePostRepository;

    private FreePost freePosts;
    private User user;
    private List<FreeComment> comments;

    @BeforeEach
    void setUp() {

        user = new User(
                1L,
                "email",
                "1234",
                "nickname",
                false,
                Role.USER
        );

        comments = new ArrayList<>();
        comments.add(new FreeComment());

        freePosts = new FreePost(
                null,
                user,
                "자유 게시글 내용",
                100,
                comments);

        // null로 된 부분 해결을 위한 코드
        freePosts = freePostRepository.save(freePosts);
    }

    @Test
    @DisplayName("자유 게시글 페이지네이션 테스트")
    void freePostPagination() {
        // given
        List<FreePost> freePostList = new ArrayList<>();

        // 위에 하나 생성 되어 있는 관계로 19개 추가.
        for (int i = 1; i < 20; i++) {
            FreePost freePost = new FreePost(
                    null,
                    user,
                    "자유 게시글 내용 " + i,
                    100 + i,
                    comments);
            freePostList.add(freePost);
        }

        freePostRepository.saveAll(freePostList);

        Pageable firstPageList = PageRequest.of(0, 5);
        Pageable secondPageList= PageRequest.of(1, 10);

        // when
        Page<FreePost> firstPage = freePostRepository.getFreePosts(firstPageList);
        Page<FreePost> secondPage = freePostRepository.getFreePosts(secondPageList);

        // then
        assertThat(firstPage.getContent().size()).isEqualTo(5);
        assertThat(firstPage.getTotalElements()).isEqualTo(20);
        assertThat(firstPage.getTotalPages()).isEqualTo(4);
        assertThat(firstPage.getNumber()).isEqualTo(0);

        assertThat(secondPage.getContent().size()).isEqualTo(10);
        assertThat(secondPage.getTotalElements()).isEqualTo(20);
        assertThat(secondPage.getTotalPages()).isEqualTo(2);
        assertThat(secondPage.getNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("자유 게시글 생성")
    void createFreePost() {
        // given
        FreePost createdFreePost = new FreePost(null, user,"새 게시글 내용", 0, comments);

        // when
        FreePost savedFreePost = freePostRepository.save(createdFreePost);

        // then
        assertThat(savedFreePost.getId()).isNotNull();
        assertThat(savedFreePost.getContent()).isEqualTo("새 게시글 내용");
    }

    @Test
    @DisplayName("자유 게시글 조회")
    void getFreePost() {
        // given
        FreePost freePost = new FreePost(null, user, "조회할 게시글 내용", 10,comments);
        freePostRepository.save(freePost);

        // when
        FreePost foundFreePost = freePostRepository.findById(freePost.getId()).orElse(null);

        // then
        assertThat(foundFreePost).isNotNull();
        assertThat(foundFreePost.getContent()).isEqualTo("조회할 게시글 내용");
    }

    @Test
    @DisplayName("자유 게시글 수정")
    void updateReviewPost() {
        // given
        FreePost freePost = new FreePost(null, user, "수정할 자유 게시글 내용", 20, comments);
        freePostRepository.save(freePost);

        // when
        freePost.update("수정된 리뷰 내용", 100);
        FreePost updatedFreePost = freePostRepository.save(freePost);

        // then
        assertThat(updatedFreePost.getContent()).isEqualTo("수정된 리뷰 내용");
    }

    @Test
    @DisplayName("자유 게시글 삭제")
    void deleteReviewPost() {
        // given
        FreePost freePost = new FreePost(null, user, "삭제할 자유 게시글 내용", 30, comments);
        freePostRepository.save(freePost);

        // when
        freePostRepository.delete(freePost);

        // then
        Optional<FreePost> deletedFreePost = freePostRepository.findById(freePost.getId());
        assertThat(deletedFreePost).isEmpty();
    }

    @Test
    @DisplayName("모든 리뷰 게시글 조회")
    void readAllReviewPosts() {
        // given
        FreePost freePost1 = new FreePost(null, user, "자유 게시글 내용 1", 40, comments);
        FreePost freePost2 = new FreePost(null, user, "자유 게시글 내용 2", 40, comments);

        freePostRepository.saveAll(Arrays.asList(freePost1, freePost2));

        // when
        List<FreePost> freePostList = freePostRepository.findAll();

        // then
        assertThat(freePostList).hasSize(3);

    }

}
