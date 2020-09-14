package life.majiang.community.model;

public class DateCount {
     private String date;
     private Integer value;

 public DateCount(String date, Integer value) {
   this.date = date;
   this.value = value;
  }

  public DateCount() {
 }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
