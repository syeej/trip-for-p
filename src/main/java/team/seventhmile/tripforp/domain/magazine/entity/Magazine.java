package team.seventhmile.tripforp.domain.magazine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import team.seventhmile.tripforp.domain.magazine.dto.MagazineDto;

@Entity
@Table(name = "magazines")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Magazine {

	// 게시글 id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name = "magazine_post_id")
	private Long id;

	// 회원 id

	// 제목
	@Column(nullable = false)
	private String title;

	// 내용
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	// 게시글 생성일
	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	// 게시글 수정일
	@LastModifiedDate
	@Column
	private LocalDateTime updatedAt;


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
			throw new IllegalArgumentException(fieldName + " cannot be null or empty");
		}
	}
}
