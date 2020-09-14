package life.majiang.community.controller;

import life.majiang.community.mapper.TuijianMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Tuijian;
import life.majiang.community.model.User;
import life.majiang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class WebUserRejisterController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TuijianMapper tuijianMapper;
    @GetMapping("/register")
    public String login(@RequestParam(name = "userName") String userName,@RequestParam(name = "userPhone") String userPhone,
                           @RequestParam(name = "userPassword") String userPassword, HttpServletRequest request, HttpServletResponse response) {
        User user = new User();
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        user.setName(userName);
        user.setAccountId("0");
        user.setPhone(userPhone);
        user.setPassword(userPassword);
        user.setGmtCreate(System.currentTimeMillis());
        user.setGmtModified(System.currentTimeMillis());
        int x=(int)(Math.random()*7);
        user.setAvatarUrl("/images/"+x+".png ");

        user.setRole("user");
        user.setBio("webUser");
        user.setIntegral(0);
        user.setFace("0");
         // 号码没有人注册过
         userMapper.insert(user);
         //建立推荐
        Tuijian tuijian=new Tuijian();
        tuijian.setUserId(user.getId());
        tuijian.setTab1("null");
        tuijian.setTab2("null");
        tuijian.setTab3("null");
        tuijianMapper.insert(tuijian);

         response.addCookie(new Cookie("token", token));
         request.getSession().setAttribute("user",user);
         return "redirect:/";
   }
}
