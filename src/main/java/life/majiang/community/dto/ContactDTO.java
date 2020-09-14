package life.majiang.community.dto;

import life.majiang.community.model.User;

/**
 * Created by codedrinker on 2019/5/7.
 */
public class ContactDTO {
    private Long id;
    private Long gmt;
    private User user1;
    private User user2;
    private String socketId;
    private int flagreadnumber;

    public String getSocketId() {
        return socketId;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGmt() {
        return gmt;
    }

    public void setGmt(Long gmt) {
        this.gmt = gmt;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public int getFlagreadnumber() {
        return flagreadnumber;
    }

    public void setFlagreadnumber(int flagreadnumber) {
        this.flagreadnumber = flagreadnumber;
    }
}
