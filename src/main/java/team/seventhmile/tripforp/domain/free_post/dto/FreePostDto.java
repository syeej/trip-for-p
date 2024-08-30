package team.seventhmile.tripforp.domain.free_post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comments;
import team.seventhmile.tripforp.domain.free_comment.entity.FreeComment;
import team.seventhmile.tripforp.domain.free_post.entity.FreePost;
import team.seventhmile.tripforp.domain.user.entity.User;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreePostDto {

    private Long id;

    private Long userId;

    private String content;

    private Integer views;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    private List<FreeComment> comments;

    // DTO -> Entity
    public FreePost toEntity() {
        return FreePost.builder()
                .id(this.id)
                .content(this.content)
                .views(this.views)
                .build();
    }

    // Entity -> DTO
    public static FreePostDto fromEntity(FreePost freePost) {
        return FreePostDto.builder()
                .id(freePost.getId())
                .content(freePost.getContent())
                .views(freePost.getViews())
                .build();
    }

}
