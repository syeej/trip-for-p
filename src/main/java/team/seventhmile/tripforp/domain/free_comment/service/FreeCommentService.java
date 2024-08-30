package team.seventhmile.tripforp.domain.free_comment.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.seventhmile.tripforp.domain.free_comment.dto.FreeCommentDto;
import team.seventhmile.tripforp.domain.free_comment.entity.FreeComment;
import team.seventhmile.tripforp.domain.free_comment.repository.FreeCommentRepository;
import team.seventhmile.tripforp.domain.free_post.entity.FreePost;
import team.seventhmile.tripforp.domain.user.entity.User;  // 올바른 User import

@Service
@Transactional(readOnly = true)
public class FreeCommentService {

	private final FreeCommentRepository freeCommentRepository;

	@Autowired
	public FreeCommentService(FreeCommentRepository freeCommentRepository) {
		this.freeCommentRepository = freeCommentRepository;
	}

	// 자유 게시판 댓글 조회
	public List<FreeCommentDto> getCommentsByPost(FreePost freePost) {
		List<FreeComment> comments = freeCommentRepository.findByFreePost(freePost);
		return comments.stream()
			.map(this::mapToDto)
			.collect(Collectors.toList());
	}

	// 자유 게시판 댓글 작성
	@Transactional
	public FreeCommentDto createComment(FreePost freePost, FreeCommentDto freeCommentDto,
		User user) {
		FreeComment freeComment = mapToEntity(freeCommentDto);
		freeComment.setFreePost(freePost);
		freeComment.setAuthor(user);
		FreeComment savedComment = freeCommentRepository.save(freeComment);
		return mapToDto(savedComment);
	}

	// 자유 게시판 댓글 수정
	@Transactional
	public FreeCommentDto updateComment(Long id, FreeCommentDto updatedCommentDto, User user) {
		FreeComment existingComment = freeCommentRepository.findByIdAndAuthor(id, user)
			.orElseThrow(() -> new RuntimeException("Comment not found or not owned by user"));

		existingComment.setContent(updatedCommentDto.getContent());
		FreeComment updatedComment = freeCommentRepository.save(existingComment);
		return mapToDto(updatedComment);
	}

	// 자유 게시판 댓글 삭제
	@Transactional
	public void deleteComment(Long id, User user) {
		FreeComment freeComment = freeCommentRepository.findByIdAndAuthor(id, user)
			.orElseThrow(() -> new RuntimeException("Comment not found or not owned by user"));
		freeCommentRepository.delete(freeComment);
	}


	// Entity -> Dto 변환
	private FreeCommentDto mapToDto(FreeComment freeComment) {
		return FreeCommentDto.builder()
			.id(freeComment.getId())
			.content(freeComment.getContent())
			.postId(freeComment.getFreePost().getId())
			.authorId(freeComment.getAuthor().getId())
			.build();
	}

	// Dto -> Entity 변환
	private FreeComment mapToEntity(FreeCommentDto freeCommentDto) {
		FreeComment freeComment = new FreeComment();
		freeComment.setContent(freeCommentDto.getContent());
		return freeComment;
	}
}
