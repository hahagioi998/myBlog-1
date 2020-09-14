package life.majiang.community.model;

import java.util.List;

public class Tagclass {
    private String categoryName;
    private List<Echarts> tags;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Echarts> getTags() {
        return tags;
    }

    public void setTags(List<Echarts> tags) {
        this.tags = tags;
    }
}
