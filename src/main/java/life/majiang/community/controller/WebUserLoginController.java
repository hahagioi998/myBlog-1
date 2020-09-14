package life.majiang.community.controller;

import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.model.UserExample;
import life.majiang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Controller
public class WebUserLoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @ResponseBody//因为没加这个，一直错
    @RequestMapping(value = "/login", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> login(@RequestBody Map<String,Object> requestMap, HttpServletRequest request,HttpServletResponse response, Model model) {
        String userPhone = requestMap.get("userPhone").toString();
        String userPassword = requestMap.get("userPassword").toString();
        User user = new User();
        user.setPhone(userPhone);
        user.setPassword(userPassword);
        List<User> users= userService.checkWebUserExist(user);
        Map<String, Object> resultMap1 = new HashMap<>();
        if (users.size() == 0) {
            // 登录失败，重新登录
            resultMap1.put("code",201);
            return resultMap1;
        } else {
            //更新
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            User dbUser = users.get(0);
            User updateUser = new User();
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            updateUser.setRole(user.getRole());
            updateUser.setToken(user.getToken());
            UserExample userExample1= new UserExample();
            userExample1.createCriteria()
                    .andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(updateUser, userExample1);
            response.addCookie(new Cookie("token", token));
            request.getSession().setAttribute("user", dbUser);
            HttpSession session=request.getSession();
       //     System.out.println(session.getAttribute("user"));
            resultMap1.put("code",200);
            return resultMap1;
        }

   }
    @GetMapping("/smslogin/{userPhone}")
    public String smslogin(@PathVariable(name = "userPhone") String userPhone, HttpServletRequest request, HttpServletResponse response) {
        User user = new User();
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        user.setPhone(userPhone);
       //更新
        UserExample userExample=new UserExample();
        userExample.createCriteria().andPhoneEqualTo(userPhone);
        List<User> users=userMapper.selectByExample(userExample);
         User dbUser = users.get(0);
         User updateUser = new User();
         updateUser=dbUser;
         updateUser.setGmtModified(System.currentTimeMillis());
         UserExample userExample1= new UserExample();
         userExample1.createCriteria()
                 .andIdEqualTo(updateUser.getId());
         userMapper.updateByExampleSelective(updateUser, userExample1);
         response.addCookie(new Cookie("token", token));
         request.getSession().setAttribute("user", updateUser);
         return "redirect:/";
    }
}
