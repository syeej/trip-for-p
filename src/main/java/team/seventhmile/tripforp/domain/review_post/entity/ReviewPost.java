package team.seventhmile.tripforp.domain.review_post.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import team.seventhmile.tripforp.domain.file.entity.MagazineFile;
import team.seventhmile.tripforp.domain.file.entity.ReviewFile;
import team.seventhmile.tripforp.domain.plan.entity.Plan;
import team.seventhmile.tripforp.domain.review_comment.entity.ReviewComment;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.global.common.BaseEntity;

import java.util.List;
import team.seventhmile.tripforp.global.exception.ValidationUtils;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "review_posts")
public class ReviewPost extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_post_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "plan_id", nullable = false)
	private Plan plan;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	@ColumnDefault("0")
	private Integer views;

	@OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewComment> comments;

	@OneToMany(mappedBy = "reviewPost", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ReviewFile> files = new ArrayList<>();


	/**
	 * 리뷰 게시글의 제목, 내용, 첨부 파일 목록을 수정합니다.
	 *
	 * @param title 수정할 제목
	 * @param content 수정할 내용
	 * @param files 첨부할 파일 목록
	 */
	public void update(String title, String content) {
		// 필드 유효성 검사
		ValidationUtils.validateField(title, "Title");
		ValidationUtils.validateField(content, "Content");

		// 제목, 내용 및 파일 목록 업데이트
		this.title = title;
		this.content = content;
	}

	public void addFile(ReviewFile file) {
		this.files.add(file);
		file.setReviewPost(this);
	}

	public void clearFile() {
		this.files.clear();
	}

	/**
	 * 리뷰 게시글의 조회 수를 1 증가시킵니다.
	 */
	public void incrementViews() {
		this.views += 1;
	}
}
