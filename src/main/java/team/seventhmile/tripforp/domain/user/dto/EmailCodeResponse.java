package team.seventhmile.tripforp.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailCodeResponse {
    private String status;
    private String message;
}
