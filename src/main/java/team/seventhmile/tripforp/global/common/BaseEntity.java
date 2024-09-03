package team.seventhmile.tripforp.global.common;

import jakarta.persistence.MappedSuperclass;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class BaseEntity {
    @CreationTimestamp(source = SourceType.DB)
    private ZonedDateTime createdAt;
    @UpdateTimestamp(source = SourceType.DB)
    private ZonedDateTime updatedAt;
}
