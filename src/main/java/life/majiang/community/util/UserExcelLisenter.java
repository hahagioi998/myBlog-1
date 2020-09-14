package life.majiang.community.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.websocket.WebSocketOneToOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;
@RestController
public class UserExcelLisenter extends AnalysisEventListener<User> {
    //一行一行读取excel内容
    //spring中处理service,controller可以调用mapper,其她类调用需要添加
    // @PostConstruct
    //    public void init() {
    //        webSocketOneToOne = this;
    //    }
    //引用时需要写webSocketOneToOne.xxxmapper
    @Autowired
    private UserMapper userMapper;

    public static UserExcelLisenter userExcelLisenter;

    @PostConstruct
     public void init() {
        userExcelLisenter = this;
     }

    @Override
    public void invoke(User data, AnalysisContext analysisContext) {
        User user=new User();
        user.setBio("webUser");
        user.setPhone(data.getPhone());
        user.setAccountId("0");
        user.setRole("user");
        user.setFace("0");
        user.setIntegral(0);
        user.setAvatarUrl("/images/4.png");
        user.setGmtCreate(System.currentTimeMillis());
        user.setGmtModified(System.currentTimeMillis());
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        user.setPassword(data.getPassword());
        user.setName(data.getName());
        userExcelLisenter.userMapper.insert(user);
    }
    //读取表头内容
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("表头："+headMap);
    }
    //读取完成之后
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) { }
}
