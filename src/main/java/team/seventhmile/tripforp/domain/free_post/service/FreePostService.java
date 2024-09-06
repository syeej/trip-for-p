package team.seventhmile.tripforp.domain.free_post.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.free_post.dto.FreePostDto;
import team.seventhmile.tripforp.domain.free_post.entity.FreePost;
import team.seventhmile.tripforp.domain.free_post.repository.FreePostRepository;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewPost;
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

	/**
	 * 자유 게시글을 작성합니다.
	 * @param freePostDto 게시글 DTO
	 * @param userEmail 작성자 이메일
	 * @return 작성된 자유 게시글의 DTO
	 */
	@Transactional
	public FreePostDto createFreePost(FreePostDto freePostDto, String userEmail) {

		// 현재 로그인된 사용자 가져오기
		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException(User.class));

		// 자유 게시글 생성
		FreePost freePost = freePostDto.convertToEntity(user);

		freePostRepository.save(freePost);

		return FreePostDto.convertToDto(freePost);
	}

	/**
	 * 자유 게시글을 수정합니다.
	 * 작성자만 수정할 수 있습니다.
	 * @param id 수정할 게시글의 ID
	 * @param freePostDto 수정할 게시글의 DTO
	 * @param userEmail 작성자 이메일
	 * @return 수정된 자유 게시글의 DTO
	 */
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

		// 게시글 수정
		freePost.update(freePostDto.getContent());

		freePostRepository.save(freePost);

		return FreePostDto.convertToDto(freePost);
	}

	/**
	 * 자유 게시글을 삭제합니다.
	 * 작성자 또는 ADMIN만 삭제할 수 있습니다.
	 * @param id 삭제할 게시글의 ID
	 * @param userEmail 요청자 이메일
	 */
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

	/**
	 * 모든 자유 게시글을 페이지네이션하여 조회합니다.
	 * @param pageable 페이징 정보
	 * @return 페이지네이션된 자유 게시글 목록
	 */
	@Transactional(readOnly = true)
	public Page<FreePostDto> getAllFreePost(Pageable pageable) {

		return freePostRepository.getFreePosts(pageable)
			.map(FreePostDto::convertToDto);

	}

	/**
	 * 특정 ID의 자유 게시글을 상세 조회합니다.
	 * 조회할 때마다 조회 수가 증가합니다.
	 * @param id 조회할 게시글의 ID
	 * @return 자유 게시글 DTO
	 */
	@Transactional
	public FreePostDto getFreePostDetail(Long id) {
		FreePost freePost = freePostRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(FreePost.class));

		// 조회 수 증가
		freePost.incrementViews();

		return FreePostDto.convertToDto(freePost);
	}

	/**
	 * 키워드를 포함하는 자유 게시글을 페이지네이션하여 검색합니다.
	 * @param keyword 검색 키워드
	 * @param pageable 페이징 정보
	 * @return 검색된 자유 게시글 목록
	 */
	@Transactional(readOnly = true)
	public Page<FreePostDto> getFreePostSearch(String keyword, Pageable pageable) {

		if (keyword == null || keyword.trim().isEmpty()) {
			return Page.empty(pageable);
		}

		Page<FreePost> freePosts = freePostRepository.getFreePostKeywordContaining(keyword.trim(),
			pageable);
		return freePosts.map(FreePostDto::convertToDto);

	}

	/**
	 * 특정 ID의 자유 게시글 엔티티를 조회합니다.
	 * @param id 조회할 게시글의 ID
	 * @return 자유 게시글 엔티티
	 */
	@Transactional(readOnly = true)
	public FreePost getFreePostEntity(Long id) {
		return freePostRepository.findById(id)

			.orElseThrow(() -> new ResourceNotFoundException(FreePost.class, id));
	}

}
