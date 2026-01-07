package Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReactionBean {
    private int reactId;
    private int userId;
    private int videoId;
    private String type; // "Like" or "Dislike"
}
