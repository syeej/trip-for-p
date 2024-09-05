package team.seventhmile.tripforp.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.seventhmile.tripforp.domain.user.entity.User;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    private String password;
    private String nickname;

    public static UserInfoResponse from(User user){
        return UserInfoResponse.builder()
                .password(user.getPassword())
                .nickname(user.getNickname())
                .build();
    }
}
