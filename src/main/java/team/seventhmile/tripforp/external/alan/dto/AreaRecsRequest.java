package team.seventhmile.tripforp.external.alan.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AreaRecsRequest {

    @NotNull(message = "출발 날짜는 필수입니다.")
    @FutureOrPresent(message = "출발 날짜는 현재 또는 미래의 날짜여야 합니다.")
    private LocalDate startDate;

    @NotNull(message = "도착 날짜는 필수입니다.")
    @Future(message = "도착 날짜는 미래의 날짜여야 합니다.")
    private LocalDate endDate;

    @NotBlank(message = "지역은 필수입니다.")
    @Pattern(regexp = "^(서울|경기|인천|강원|충북|충남|대전|경북|경남|대구|울산|부산|전북|전남|광주|제주|세종)$",
        message = "유효하지 않은 지역입니다.")
    private String area;

}
