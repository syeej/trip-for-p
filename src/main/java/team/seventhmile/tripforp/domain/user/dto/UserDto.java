package team.seventhmile.tripforp.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.seventhmile.tripforp.domain.user.entity.Role;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {

	private String email;
	private String password;
	private String nickname;
	private Role role;

}
