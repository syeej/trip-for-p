package team.seventhmile.tripforp.domain.free_post.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.free_post.dto.FreePostDto;
import team.seventhmile.tripforp.domain.free_post.entity.FreePost;
import team.seventhmile.tripforp.domain.free_post.repository.FreePostRepository;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;


@Service
public class FreePostService {

	private final FreePostRepository freePostRepository;
	private final UserRepository userRepository;

	@Autowired
	public FreePostService(FreePostRepository freePostRepository, UserRepository userRepository) {
		this.freePostRepository = freePostRepository;
		this.userRepository = userRepository;
	}

	// 자유 게시글 생성
	@Transactional
	public FreePostDto createFreePost(FreePostDto freePostDto) {

		User user = userRepository.findById(freePostDto.getUserId())
			.orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

		FreePost freePost = freePostDto.toEntity(user);
		freePostRepository.save(freePost);

		return FreePostDto.fromEntity(freePost);
	}

	// 자유 게시글 수정
	@Transactional
	public FreePostDto updateFreePost(Long id, FreePostDto freePostDto) {

		// 현재 사용자 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentUserEmail = authentication.getName();

		// 게시글 존재 여부 확인
		FreePost freePost = freePostRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("데이터를 찾을 수 없습니다."));

		// 작성자 확인
		if (!freePost.getUser().getEmail().equals(currentUserEmail)) {
			throw new SecurityException("본인의 글만 수정할 수 있습니다.");
		}

		// 업데이트 로직
		User user = userRepository.findById(freePostDto.getUserId())
			.orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

		freePost.update(
			freePostDto.getContent(),
			freePostDto.getViews()
		);

		freePost.setUser(user);

		freePostRepository.save(freePost);

		return FreePostDto.fromEntity(freePost);
	}

	// 자유 게시글 삭제
	@Transactional
	public void deleteFreePost(Long id) {

		// 현재 사용자 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentUserEmail = authentication.getName();

		// 게시글 존재 여부 확인
		FreePost freePost = freePostRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("데이터를 찾을 수 없습니다."));

		// 사용자 정보 확인
		User currentUser = userRepository.findByEmail(currentUserEmail)
			.orElseThrow(()-> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

		// 권한 확인: ADMIN 또는 작성자
		if (!freePost.getUser().getEmail().equals(currentUserEmail)
			&& currentUser.getRole() != Role.ADMIN) {
			throw new SecurityException("삭제 권한이 없습니다.");
		}

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

		Page<FreePost> freePosts = freePostRepository.getFreePostKeywordContaining(keyword.trim(),
			pageable);
		return freePosts.map(FreePostDto::fromEntity);

	}

	// FreePost 엔티티 조회
	@Transactional(readOnly = true)
	public FreePost getFreePostEntity(Long id) {
		return freePostRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("데이터를 찾을 수 없습니다."));
	}

}
