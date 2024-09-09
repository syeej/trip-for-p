package team.seventhmile.tripforp.domain.plan.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import team.seventhmile.tripforp.domain.plan.dto.GetPlaceCountResponse;

public interface PlanItemRepositoryCustom {
    List<GetPlaceCountResponse> getPlaceCount(Pageable pageable);

}
