package life.majiang.community.model;

public class Echarts {
    private Long id;
    private String name;
     private Integer value;

 public Echarts(Long id,String name, Integer value) {
  this.id=id;
  this.name = name;
 this.value = value;
  }

  public Echarts() {
 }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
