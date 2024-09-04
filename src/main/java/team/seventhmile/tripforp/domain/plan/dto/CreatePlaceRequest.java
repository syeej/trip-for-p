package team.seventhmile.tripforp.domain.plan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CreatePlaceRequest {

    @NotBlank(message = "주소는 필수입니다.")
    @Size(max = 255, message = "주소는 255자를 초과할 수 없습니다.")
    private String addressName;

    @NotBlank(message = "카테고리 이름은 필수입니다.")
    @Size(max = 100, message = "카테고리 이름은 100자를 초과할 수 없습니다.")
    private String categoryName;

    @NotBlank(message = "장소 이름은 필수입니다.")
    @Size(max = 100, message = "장소 이름은 100자를 초과할 수 없습니다.")
    private String placeName;

    @NotBlank(message = "장소 URL은 필수입니다.")
    @Size(max = 255, message = "URL은 255 초과할 수 없습니다.")
    private String placeUrl;

    @NotNull(message = "경도(X 좌표)는 필수입니다.")
    private double x;

    @NotNull(message = "위도(Y 좌표)는 필수입니다.")
    private double y;

    @Builder
    public CreatePlaceRequest(String addressName, String categoryName, String placeName,
        String placeUrl, double x, double y) {
        this.addressName = addressName;
        this.categoryName = categoryName;
        this.placeName = placeName;
        this.placeUrl = placeUrl;
        this.x = x;
        this.y = y;
    }
}
