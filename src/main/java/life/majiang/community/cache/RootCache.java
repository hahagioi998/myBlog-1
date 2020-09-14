package life.majiang.community.cache;

import java.util.ArrayList;
import java.util.List;

public class RootCache {
    public List<String> get(){
        List<String> webroots=new ArrayList<>();
        webroots.add("帖子管理");
        webroots.add("标签管理");
        webroots.add("博客介绍");
        webroots.add("程序管理");
        webroots.add("用户管理");
        webroots.add("权限管理");
        webroots.add("网站视图");
        return webroots;
    }
}
