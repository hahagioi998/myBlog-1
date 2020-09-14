package life.majiang.community.dto;
import life.majiang.community.model.Question;
import life.majiang.community.model.User;
/**
 * Created by codedrinker on 2019/5/7.
 */
public class CollectDTO {
    private Long id;
    private Long time;
    private User user;
    private Question question;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
