package life.majiang.community.dto;

/**
 * Created by codedrinker on 2019/5/30.
 */
public class CommentCreateDTO {
    private Long parentId;
    private String content;
    private Integer type;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
