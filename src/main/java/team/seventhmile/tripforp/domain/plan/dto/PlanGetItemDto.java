package team.seventhmile.tripforp.domain.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanGetItemDto {
    private Long id;
    private PlaceGetDto place;
    private LocalDate tripDate;
    private String memo;
    private int sequence;
}

