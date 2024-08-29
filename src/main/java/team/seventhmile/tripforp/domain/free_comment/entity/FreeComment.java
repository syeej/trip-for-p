package team.seventhmile.tripforp.domain.free_comment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.LastModifiedDate;
import team.seventhmile.tripforp.domain.free_post.entity.FreePost;
import team.seventhmile.tripforp.domain.user.entity.User;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "free_post_comments")
public class FreeComment {

	// 댓글 id (pk)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	// 자유 게시글 id (fk)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "free_post_id", nullable = false)
	private FreePost freePost;

	// 회원 id (fk)
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User author;

	// 내용
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	// 댓글 생성일
	@LastModifiedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	// 댓글 수정일
	@LastModifiedDate
	@Column
	private LocalDateTime updatedAt;

}
