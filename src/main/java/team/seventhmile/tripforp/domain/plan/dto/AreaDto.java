package team.seventhmile.tripforp.domain.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.seventhmile.tripforp.domain.plan.entity.Area;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AreaDto {
    private String name;
    private String displayName;

    // Area 엔티티를 DTO로 변환하는 메서드
    public static AreaDto fromEntity(Area area) {
        return new AreaDto(area.name(), area.getName());
    }

    // DTO를 Area 엔티티로 변환하는 메서드
    public static Area toEntity(AreaDto dto) {
        return Area.fromName(dto.getDisplayName());
    }
}