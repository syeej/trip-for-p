package team.seventhmile.tripforp.domain.magazine.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.ColumnDefault;
import team.seventhmile.tripforp.domain.file.entity.MagazineFile;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.global.common.BaseEntity;
import team.seventhmile.tripforp.global.exception.ValidationUtils;

@Entity
@Table(name = "magazines")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Magazine extends BaseEntity {

	// 게시글 id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "magazine_post_id")
	private Long id;

	// 회원 id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// 제목
	@Column(nullable = false)
	private String title;

	// 내용
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	// 조회수
	@Column(nullable = false)
	@ColumnDefault("0")
	private Integer views;

	// 첨부 파일
	@OneToMany(mappedBy = "magazine", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<MagazineFile> files = new ArrayList<>();


	/**
	 * 매거진 게시글의 제목, 내용, 첨부 파일 목록을 수정합니다.
	 *
	 * @param title 수정할 제목
	 * @param content 수정할 내용
	 * @param files 첨부할 파일 목록
	 */
	public void update(String title, String content, List<MagazineFile> files) {
		// 필드 유효성 검사
		ValidationUtils.validateField(title, "Title");
		ValidationUtils.validateField(content, "Content");

		// 제목, 내용 및 파일 목록 업데이트
		this.title = title;
		this.content = content;
		this.files.clear();
		this.files.addAll(files);
		files.forEach(file -> file.setMagazine(this));
	}

	/**
	 * 매거진 게시글의 조회 수를 1 증가시킵니다.
	 */
	public void incrementViews() {
		this.views += 1;
	}

	public void addFile(MagazineFile file) {
		this.files.add(file);
		file.setMagazine(this);
	}

	public void clearFile() {
		this.files.clear();
	}

}
