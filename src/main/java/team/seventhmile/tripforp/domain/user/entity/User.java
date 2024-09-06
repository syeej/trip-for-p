package team.seventhmile.tripforp.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import team.seventhmile.tripforp.domain.plan.dto.UserGetDto;
import team.seventhmile.tripforp.global.common.BaseEntity;

@Entity
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "users")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, unique = true)
	private String nickname;

	@Column(nullable = false)
	private Boolean isDeleted;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	protected User(String nickname, String email) {
		this.nickname = nickname;
		this.email = email;
	}
	// UserDto를 User 엔티티로 변환하는 메서드
	public static User fromDto(UserGetDto dto) {
		return User.builder()
				.nickname(dto.getNickname())
				.email(dto.getEmail())
				.build();
	}

	// User 엔티티를 UserDto로 변환하는 메서드
	public UserGetDto toDto() {
		return new UserGetDto(this.nickname, this.email);
	}

	//[마이페이지] - 개인정보수정
	public void updateNickname(String nickname){
		this.nickname = nickname;
	}
	public void updatePassword(String password){
		this.password = password;
	}

	//[마이페이지] - 회원탈퇴
	public void withdrawalUser(){
		this.isDeleted = true;
	}
}