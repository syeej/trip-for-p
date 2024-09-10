package team.seventhmile.tripforp.domain.free_post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.free_post.dto.FreePostDto;
import team.seventhmile.tripforp.domain.free_post.entity.FreePost;
import team.seventhmile.tripforp.domain.free_post.repository.FreePostRepository;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;
import team.seventhmile.tripforp.global.exception.UnauthorizedAccessException;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FreePostService {

	private final FreePostRepository freePostRepository;
	private final UserRepository userRepository;

	// 자유 게시글 생성
	@Transactional
	public FreePostDto createFreePost(FreePostDto freePostDto, String userEmail) {

		// 현재 로그인된 사용자 가져오기
		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException(User.class));

		FreePost freePost = freePostDto.convertToEntity(user);
		freePostRepository.save(freePost);

		return FreePostDto.convertToDto(freePost);
	}

	// 자유 게시글 수정
	@Transactional
	public FreePostDto updateFreePost(Long id, FreePostDto freePostDto, String userEmail) {

		FreePost freePost = freePostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(FreePost.class));

		// 현재 로그인된 사용자 가져오기
		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException(User.class));

		// 작성자 확인
		if (!freePost.getUser().getEmail().equals(userEmail)) {
			throw new UnauthorizedAccessException(FreePost.class);
		}

		// 업데이트
		freePost.update(freePostDto.getContent());

		freePostRepository.save(freePost);

		return FreePostDto.convertToDto(freePost);
	}

	// 자유 게시글 삭제
	@Transactional
	public void deleteFreePost(Long id, String userEmail) {

		// 게시글 존재 여부 확인
		FreePost freePost = freePostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(FreePost.class));

		// 현재 로그인된 사용자 가져오기
		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException(User.class));

		// 권한 확인: ADMIN 또는 작성자
		if (!freePost.getUser().getEmail().equals(userEmail)
			&& user.getRole() != Role.ADMIN) {
			throw new UnauthorizedAccessException(FreePost.class);
		}

		freePostRepository.delete(freePost);
	}

	// 자유 게시글 목록 조회
	@Transactional(readOnly = true)
	public Page<FreePostDto> getAllFreePost(Pageable pageable) {

		return freePostRepository.getFreePosts(pageable)
			.map(FreePostDto::convertToDto);

	}

	// 자유 게시글 상세 조회
	@Transactional
	public FreePostDto getFreePostDetail(Long id) {
		FreePost freePost = freePostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(FreePost.class));

		// 조회 수 증가
		freePost.incrementViews();

		return FreePostDto.convertToDto(freePost);
	}

	// 자유 게시글 검색(제목, 내용) 조회
	@Transactional(readOnly = true)
	public Page<FreePostDto> getFreePostSearch(String keyword, Pageable pageable) {

		if (keyword == null || keyword.trim().isEmpty()) {
			return Page.empty(pageable);
		}

		Page<FreePost> freePosts = freePostRepository.getFreePostKeywordContaining(keyword.trim(),
			pageable);
		return freePosts.map(FreePostDto::convertToDto);

	}

	// FreePost 엔티티 조회
	@Transactional(readOnly = true)
	public FreePost getFreePostEntity(Long id) {
		return freePostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(FreePost.class, id));
	}

	// 내가 작성한 자유게시글 조회
	@Transactional(readOnly = true)
	public Page<FreePostDto> getMyFreePostList(UserDetails user, Pageable pageable) {
		return freePostRepository.findByUserEmailOrderByCreatedAtDesc(user.getUsername(), pageable)
				.map(FreePostDto::convertToDto);
	}

}
