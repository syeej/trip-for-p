package team.seventhmile.tripforp.domain.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.seventhmile.tripforp.domain.user.dto.UserDto;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserGetDto extends UserDto {
    private String email;
    private String nickname;

}
