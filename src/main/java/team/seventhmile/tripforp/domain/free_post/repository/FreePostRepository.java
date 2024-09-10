package team.seventhmile.tripforp.domain.free_post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.free_post.entity.FreePost;


@Repository
public interface FreePostRepository extends JpaRepository<FreePost, Long>, FreePostRepositoryCustom {

    //[마이페이지]내가 작성한 자유게시글 조회
    Page<FreePost> findByUserEmailOrderByCreatedAtDesc(String email, Pageable pageable);
}
