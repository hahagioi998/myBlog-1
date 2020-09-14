package life.majiang.community.interceptor;


import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.model.UserExample;
import life.majiang.community.service.ContactService;
import life.majiang.community.service.NoticeService;
import life.majiang.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by codedrinker on 2019/5/16.
 */
@Service
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;
   @Autowired
    private NotificationService notificationService;
    @Autowired
    private ContactService contactService;
    @Autowired
    private NoticeService noticeService;
   /* @Autowired
    private AdService adService;

    @Value("${github.redirect.uri}")
    private String redirectUri;*/
    //拦截器，跳转页面时进行拦截验证
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //设置 context 级别的属性
      /*  request.getServletContext().setAttribute("redirectUri", redirectUri);
        // 没有登录的时候也可以查看导航
        for (AdPosEnum adPos : AdPosEnum.values()) {
            request.getServletContext().setAttribute(adPos.name(), adService.list(adPos.name()));
        }*/
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0)
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    UserExample userExample = new UserExample();
                    userExample.createCriteria()
                            .andTokenEqualTo(token);
                    List<User> users = userMapper.selectByExample(userExample);
                    if (users.size() != 0) {
                        //通知消息数：最新回复+最新私信+最新通知+最新点赞+我的粉丝
                        request.getSession().setAttribute("user", users.get(0));
                        int unreadCount = notificationService.unreadCount(users.get(0).getId());
                        int unreadMessageCount = contactService.unreadMessageCount(users.get(0).getId());

                        int unreadNoticeCount = noticeService.unreadNoticeCount(users.get(0).getId());
                        int unreadZanCount = noticeService.unreadZanCount(users.get(0).getId());
                       // int unreadFenCount = noticeService.unreadFenCount(users.get(0).getId());

                        request.getSession().setAttribute("Count", unreadCount+unreadMessageCount+unreadNoticeCount+unreadZanCount);
                        request.getSession().setAttribute("unreadCount", unreadCount);
                        request.getSession().setAttribute("unreadZanCount", unreadZanCount);
                       // request.getSession().setAttribute("unreadFenCount", unreadFenCount);
                        request.getSession().setAttribute("unreadNoticeCount", unreadNoticeCount);
                        request.getSession().setAttribute("unreadMessageCount", unreadMessageCount);
                    }
                    break;
                }
            }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

    }
}
