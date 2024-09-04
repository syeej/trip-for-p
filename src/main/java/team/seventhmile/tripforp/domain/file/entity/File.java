package team.seventhmile.tripforp.domain.file.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.seventhmile.tripforp.domain.review_post.entity.ReviewFile;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 원본 파일명
	@Column(nullable = false)
	private String originalFileName;

	// 고유 파일명 (서버에 저장될 이름)
	@Column(nullable = false)
	private String uniqueFileName;

	// 파일 유형 (ex: jpeg, mp4)
	@Column(nullable = false)
	private String fileType;

	// 파일이 저장된 경로
	@Column(nullable = false)
	private String filePath;

	@OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewFile> reviewFiles;
}
