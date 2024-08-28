package team.seventhmile.tripforp.external.alan.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
public class AlanApiResponse {

    private Action action;
    private String content;

    public AlanApiResponse(Action action, String content) {
        this.action = action;
        this.content = content;
    }

    @NoArgsConstructor
    @Getter
    public static class Action {

        private String name;
        private String speak;
    }
}
