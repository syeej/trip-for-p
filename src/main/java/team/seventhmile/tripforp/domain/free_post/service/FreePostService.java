package team.seventhmile.tripforp.domain.free_post.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.free_post.dto.FreePostDto;
import team.seventhmile.tripforp.domain.free_post.entity.FreePost;
import team.seventhmile.tripforp.domain.free_post.repository.FreePostRepository;


@Service
public class FreePostService {

    private final FreePostRepository freePostRepository;

    @Autowired
    public FreePostService(FreePostRepository freePostRepository) {
        this.freePostRepository = freePostRepository;
    }

    // 자유 게시글 생성
    @Transactional
    public FreePostDto createFreePost(FreePostDto freePostDto) {

        FreePost freePost = freePostDto.toEntity();
        freePostRepository.save(freePost);

        return FreePostDto.fromEntity(freePost);

    }

    // 자유 게시글 수정
    @Transactional
    public FreePostDto updateFreePost(Long id, FreePostDto freePostDto) {

        FreePost freePost = freePostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("데이터를 찾을 수 없습니다."));

        freePost.update(
                freePostDto.getContent(),
                freePostDto.getViews()
        );

        freePostRepository.save(freePost);

        return FreePostDto.fromEntity(freePost);

    }

    // 자유 게시글 삭제
    @Transactional
    public void deleteFreePost(Long id) {

        FreePost freePost = freePostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("데이터를 찾을 수 없습니다."));
        freePostRepository.delete(freePost);

    }

    // 자유 게시글 목록 조회
    @Transactional(readOnly = true)
    public Page<FreePostDto> getAllFreePost(Pageable pageable) {

        return freePostRepository.getFreePosts(pageable)
                .map(FreePostDto::fromEntity);

    }

    // 자유 게시글 상세 조회
    @Transactional(readOnly = true)
    public FreePostDto getFreePostDetail(Long id) {

        return freePostRepository.findById(id)
                .map(FreePostDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("데이터를 찾을 수 없습니다."));

    }

    // 자유 게시글 검색(제목, 내용) 조회
    @Transactional(readOnly = true)
    public Page<FreePostDto> getFreePostSearch(String keyword, Pageable pageable) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty(pageable);
        }

        Page<FreePost> freePosts = freePostRepository.getFreePostKeywordContaining(keyword.trim(), pageable);
        return freePosts.map(FreePostDto::fromEntity);

    }


}
