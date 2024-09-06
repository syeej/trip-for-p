package team.seventhmile.tripforp.domain.file.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.seventhmile.tripforp.domain.magazine.entity.Magazine;

@Entity
@Table(name = "magazine_files")
@Getter
@NoArgsConstructor
public class MagazineFile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String fileName;

	@Column
	private String url;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "magazine_id")
	private Magazine magazine;

	@Builder
	public MagazineFile(String fileName, String url) {
		this.fileName = fileName;
		this.url = url;
	}
}
