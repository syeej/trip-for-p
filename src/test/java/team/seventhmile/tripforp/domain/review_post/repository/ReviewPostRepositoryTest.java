package team.seventhmile.tripforp.domain.review_post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.review_post.entity.QReviewPost;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;
import team.seventhmile.tripforp.global.config.QuerydslConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application.yml")
@Import(QuerydslConfig.class)
public class ReviewPostRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private ReviewPostRepository reviewPostRepository;

    private final QReviewPost qReviewPost = QReviewPost.reviewPost;

    private Plan plan;
    private ReviewPost reviewPosts;

    @BeforeEach
    void setUp() {
        plan = Plan.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .area("부산광역시")
                .title("부산 여행")
                .build();
        testEntityManager.persist(plan);

        reviewPosts = new ReviewPost(
                null,
                plan,
                "리뷰 게시글 제목",
                "리뷰 게시글 내용",
                100,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1));

        // null로 된 부분 해결을 위한 코드
        reviewPosts = reviewPostRepository.save(reviewPosts);
    }

    @Test
    @DisplayName("리뷰 게시글 페이지네이션 테스트")
    void reviewPostPagination() {
        // given
        List<ReviewPost> reviewPostList = new ArrayList<>();

        // 위에 하나 생성 되어 있는 관계로 19개 추가.
        for (int i = 1; i < 20; i++) {
            ReviewPost reviewPost = new ReviewPost(
                    null,
                    plan,
                    "리뷰 게시글 제목 " + i,
                    "리뷰 게시글 내용 " + i,
                    100 + i,
                    LocalDateTime.now().minusDays(i),
                    LocalDateTime.now());
            reviewPostList.add(reviewPost);
        }

        reviewPostRepository.saveAll(reviewPostList);

        Pageable firstPageList = PageRequest.of(0, 5);
        Pageable secondPageList= PageRequest.of(1, 10);

        // when
        Page<ReviewPost> firstPage = reviewPostRepository.getReviewPosts(firstPageList);
        Page<ReviewPost> secondPage = reviewPostRepository.getReviewPosts(secondPageList);

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
    @DisplayName("리뷰 게시글 생성")
    void createReviewPost() {
        // given
        ReviewPost createdReviewPost = new ReviewPost(null, plan, "새 리뷰 제목", "새 리뷰 내용", 0, LocalDateTime.now(), LocalDateTime.now());

        // when
        ReviewPost savedReviewPost = reviewPostRepository.save(createdReviewPost);

        // then
        assertThat(savedReviewPost.getId()).isNotNull();
        assertThat(savedReviewPost.getTitle()).isEqualTo("새 리뷰 제목");
        assertThat(savedReviewPost.getContent()).isEqualTo("새 리뷰 내용");
    }

    @Test
    @DisplayName("리뷰 게시글 조회")
    void readReviewPost() {
        // given
        ReviewPost reviewPost = new ReviewPost(null, plan, "조회할 리뷰 제목", "조회할 리뷰 내용", 10, LocalDateTime.now(), LocalDateTime.now());
        reviewPostRepository.save(reviewPost);

        // when
        ReviewPost foundReviewPost = reviewPostRepository.findById(reviewPost.getId()).orElse(null);

        // then
        assertThat(foundReviewPost).isNotNull();
        assertThat(foundReviewPost.getTitle()).isEqualTo("조회할 리뷰 제목");
        assertThat(foundReviewPost.getContent()).isEqualTo("조회할 리뷰 내용");
    }

    @Test
    @DisplayName("리뷰 게시글 수정")
    void updateReviewPost() {
        // given
        ReviewPost reviewPost = new ReviewPost(null, plan, "수정할 리뷰 제목", "수정할 리뷰 내용", 20, LocalDateTime.now(), LocalDateTime.now());
        reviewPostRepository.save(reviewPost);

        // when
        reviewPost.update(plan, "수정된 리뷰 제목", "수정된 리뷰 내용", 100, LocalDateTime.now());
        ReviewPost updatedReviewPost = reviewPostRepository.save(reviewPost);

        // then
        assertThat(updatedReviewPost.getTitle()).isEqualTo("수정된 리뷰 제목");
        assertThat(updatedReviewPost.getContent()).isEqualTo("수정된 리뷰 내용");
    }

    @Test
    @DisplayName("리뷰 게시글 삭제")
    void deleteReviewPost() {
        // given
        ReviewPost reviewPost = new ReviewPost(null, plan, "삭제할 리뷰 제목", "삭제할 리뷰 내용", 30, LocalDateTime.now(), LocalDateTime.now());
        reviewPostRepository.save(reviewPost);

        // when
        reviewPostRepository.delete(reviewPost);

        // then
        Optional<ReviewPost> deletedReviewPost = reviewPostRepository.findById(reviewPost.getId());
        assertThat(deletedReviewPost).isEmpty();
    }

    @Test
    @DisplayName("모든 리뷰 게시글 조회")
    void readAllReviewPosts() {
        // given
        ReviewPost reviewPost1 = new ReviewPost(null, plan, "리뷰 1", "내용 1", 40, LocalDateTime.now(), LocalDateTime.now());
        ReviewPost reviewPost2 = new ReviewPost(null, plan, "리뷰 2", "내용 2", 50, LocalDateTime.now(), LocalDateTime.now());
        reviewPostRepository.saveAll(Arrays.asList(reviewPost1, reviewPost2));

        // when
        List<ReviewPost> reviewPostList = reviewPostRepository.findAll();

        // then
        assertThat(reviewPostList).hasSize(3);

    }

}
