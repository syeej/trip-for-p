package team.seventhmile.tripforp.domain.magazine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import team.seventhmile.tripforp.domain.file.entity.File;
import team.seventhmile.tripforp.domain.magazine.dto.MagazineDto;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.global.common.BaseEntity;

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

	// 첨부 파일
	@ManyToMany
	@JoinTable(
		name = "magazine_files",
		joinColumns = @JoinColumn(name = "magazine_id"),
		inverseJoinColumns = @JoinColumn(name = "file_id")
	)
	private List<File> files;


	// 수정 로직 (더티 체킹 방식)
	public <T extends Magazine> void update(MagazineDto magazineDto) {
		// Not Null 예외 처리
		validateField(magazineDto.getTitle(), "Title");
		validateField(magazineDto.getContent(), "Content");

		this.title = magazineDto.getTitle();
		this.content = magazineDto.getContent();
	}


	// Not Null 예외 처리 로직
	public static void validateField(String field, String fieldName) {
		if (field == null || field.isEmpty()) {
			throw new IllegalArgumentException("내용을 입력하세요.");
		}
	}
}
