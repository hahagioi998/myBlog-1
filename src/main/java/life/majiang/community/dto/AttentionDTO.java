package life.majiang.community.dto;
import life.majiang.community.model.User;

/**
 * Created by codedrinker on 2019/5/7.
 */
public class AttentionDTO {
    private Long id;
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
