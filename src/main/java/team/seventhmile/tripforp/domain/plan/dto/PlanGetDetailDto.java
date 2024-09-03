package team.seventhmile.tripforp.domain.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.seventhmile.tripforp.domain.plan.entity.Area;
import team.seventhmile.tripforp.domain.user.dto.UserDto;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlanGetDetailDto {
    private UserDto user;
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private AreaDto area;
    private int views;
    private int likeCount;
    private List<PlanGetItemDto> planItems;
}
