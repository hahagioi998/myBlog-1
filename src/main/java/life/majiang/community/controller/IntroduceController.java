package life.majiang.community.controller;

import life.majiang.community.cache.HotTagCache;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.mapper.NoticeMapper;
import life.majiang.community.mapper.ProjectMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.mapper.WebrootMapper;
import life.majiang.community.model.*;
import life.majiang.community.service.QuestionService;
import life.majiang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;*/
@Controller
public class IntroduceController {
    @Autowired
    private HotTagCache hotTagCache;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WebrootMapper webrootMapper;
    @Autowired
    private NoticeMapper noticeMapper;
    @Autowired
    private ProjectMapper projectMapper;
   // private static final String KEY = "abc123"; // KEY为自定义秘钥
    @GetMapping("/introduce")
    public String blogintro(Model model) {
        String section2="blogintro";
        model.addAttribute("section2",section2);
        return "blogintro";
    }
    @GetMapping("/index1")
    public String index1(Model model) {
        return "index1";
    }
    @GetMapping("/alyuploadshiyan")
    public String alyuploadshiyan() {
        return "alyuploadshiyan";
    }

    @GetMapping("/shiyan1")
    public String shiyan1() {
        return "shiyan1";
    }

    @GetMapping("/admin/{action}")
    public String admin(@PathVariable(name = "action") String action,
                        Model model,HttpServletRequest request,
                        @RequestParam(name="page", defaultValue="1") Integer page,
                        @RequestParam(name="size",defaultValue="5") Integer size,
                        @RequestParam(name="search",required = false,defaultValue="") String search,
                        @RequestParam(name="tag",required = false) String tag) {
        String manager="noroot";
        List<Map<String,String>> roots1=new ArrayList<>();
        User user = (User) request.getSession().getAttribute("user");
        roots1=root(user.getRole());
        if ("posts".equals(action)) {
            //判断有无权限
            for(int i=0;i<roots1.size();i++){
                if(roots1.get(i).get("eng").equals("posts")){
                    PaginationDTO paginationDTO=questionService.list(search,tag,page,size);
                    ArrayList<User> userList = new ArrayList<User>();
                    //new ServerListener().start();
                    model.addAttribute("pagination",paginationDTO);
                    model.addAttribute("search",search);
                    //  model.addAttribute("tag",tag);
                    manager="posts";
                }
            }
        } else if ("lables".equals(action)) {
            for(int i=0;i<roots1.size();i++){
                if(roots1.get(i).get("eng").equals("lables")){
                    List<String> tags=hotTagCache.getHots();
                    model.addAttribute("tags",tags);
                    manager="lables";
                }
            }
        }else if ("intro".equals(action)) {
            for(int i=0;i<roots1.size();i++){
                if(roots1.get(i).get("eng").equals("intro")){
                    manager="intro";
                }
            }
        }
        else if ("project".equals(action)) {
            for(int i=0;i<roots1.size();i++){
                if(roots1.get(i).get("eng").equals("project")){
                    ProjectExample projectExample=new ProjectExample();
                    projectExample.createCriteria().andIdIsNotNull();
                    List<Project> projects=projectMapper.selectByExample(projectExample);
                    model.addAttribute("projects",projects);
                    manager="project";
                }
            }
        }
        else if ("root".equals(action)) {
            for(int i=0;i<roots1.size();i++){
                if(roots1.get(i).get("eng").equals("root")){
                    //WebrootExample rootExample=new WebrootExample();
                    /*List<Webroot> roots=new ArrayList<>();
                    rootExample.createCriteria().andIdIsNotNull();  //总是报错*/
                    /*roots=webrootMapper.selectByExample(rootExample);
                    ///
                    RootCache rootCache=new RootCache();*/
                   // List<String> webroots=rootCache.get();
                    List<Webroot> guanroots=new ArrayList<>();
                    List<Webroot> puguanroots=new ArrayList<>();
                    /*for(String webroot:webroots){
                        int m=0,n=0;
                        for(Webroot root:roots){
                            if(root.getRolename().equals("admin")){
                                if(root.getFunctionname().equals(webroot)){
                                    m=1;
                                }
                            }
                            if(root.getRolename().equals("genadmin")){
                                if(root.getFunctionname().equals(webroot)){
                                    n=1;
                                }
                            }
                        }
                        if(m==0){
                            guanroots.add(webroot);
                        }
                        if(n==0){
                            puguanroots.add(webroot);
                        }
                    }*/
                    WebrootExample rootExample=new WebrootExample();
                    rootExample.createCriteria().andRolenameEqualTo("admin");
                    guanroots=webrootMapper.selectByExample(rootExample);

                    WebrootExample rootExample1=new WebrootExample();
                    rootExample1.createCriteria().andRolenameEqualTo("genadmin");
                    puguanroots=webrootMapper.selectByExample(rootExample1);
                   // model.addAttribute("roots",roots);
                    model.addAttribute("puguanroots",puguanroots);
                    model.addAttribute("guanroots",guanroots);
                    manager="root";
                }
            }

        }
        else if ("users".equals(action)) {
            for(int i=0;i<roots1.size();i++){
                if(roots1.get(i).get("eng").equals("users")){
                    PaginationDTO paginationDTO = userService.list(page,size,search);
                    model.addAttribute("search",search);
                    model.addAttribute("pagination",paginationDTO);
                    manager="users";
                }
            }
        }
        else if ("chart".equals(action)) {
            for(int i=0;i<roots1.size();i++){
                if(roots1.get(i).get("eng").equals("chart")){
                    manager="chart";
                }
            }
        }
        model.addAttribute("manager",manager);
        model.addAttribute("roots1",roots1);
        return "admin";
    }
    @GetMapping("/openProject/{projectId}")
    public String openProject(@PathVariable(name = "projectId") String projectId, Model model) {
        Project project=projectMapper.selectByPrimaryKey(Long.valueOf(projectId));
        project.setOpen(1);
         projectMapper.updateByPrimaryKey(project);
        return "redirect:/admin/project";
    }
    @GetMapping("/offProject/{projectId}")
    public String offProject(@PathVariable(name = "projectId") String projectId, Model model) {
        Project project=projectMapper.selectByPrimaryKey(Long.valueOf(projectId));
        project.setOpen(0);
        projectMapper.updateByPrimaryKey(project);
        return "redirect:/admin/project";
    }
    @PostMapping("/admin/send")
    public String send(@RequestParam(name="page", defaultValue="1") Integer page,
                       @RequestParam(name="search",required = false,defaultValue="") String search,
                        HttpServletRequest request, HttpServletResponse response) {
        String users[] = request.getParameterValues("tongzhi");
        String content=request.getParameter("content");
        String method=request.getParameter("method");
        java.text.SimpleDateFormat formatter=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date currentTime=new java.util.Date();
        String fb_time=formatter.format(currentTime);
        for(int i=0;i<users.length;i++) {
            System.out.println(users[i]+"sss");
            if(method.equals("zhannei")) {
                userService.Send(Long.valueOf(users[i]),content,fb_time);
            }else {
                //qq邮件方式，两个的qq
               /* int student_ids[]=TiebaService.getAllstudent_idBycla_id(Integer.parseInt(cla_ids[i]));
                for(int j=0;j<student_ids.length;j++) {
                    String student_qq=TiebaService.chauserStudentById(student_ids[j]).getQq();
                    student_qq=student_qq+"@qq.com";
                    String title=teacher_name+"发来通知:";
                    try {
                        SendqqMail.send(student_qq,content,title);
                    } catch (MessagingException | GeneralSecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }*/
            }
        }
        return "redirect:/admin/users?"+"page="+page+"&serach="+search;
    }
    @GetMapping("/notice_xiang/{noticeId}")
    public String notice_xiang(@PathVariable(name = "noticeId") String noticeId, Model model) {
        Notice notice=noticeMapper.selectByPrimaryKey(Long.valueOf(noticeId));
        notice.setReadflag(1);
        noticeMapper.updateByPrimaryKey(notice);
        model.addAttribute("msg",notice.getMsg());
        return "notice";
    }
    @GetMapping("/role/{action}/{id}")
    public String delect(@RequestParam(name="page", defaultValue="1") Integer page,
                         @RequestParam(name="search",required = false,defaultValue="") String search,
                         @PathVariable(name = "action") String action, @PathVariable(name = "id") String id,
                         Model model, HttpServletRequest request, HttpServletResponse response) {
        if ("user".equals(action)) {
            User user=userMapper.selectByPrimaryKey(Long.parseLong(id));
            user.setRole("genadmin");
            userMapper.updateByPrimaryKey(user);
            //发送通知：
            Notice notice=new Notice();
            notice.setReadflag(0);
            java.text.SimpleDateFormat formatter=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date currentTime=new java.util.Date();
            notice.setGmtCreat(formatter.format(currentTime));
            notice.setMsg("你被管理员升级为网站普通管理员");
            notice.setUserId(Long.parseLong(id));
            noticeMapper.insert(notice);
        } else if ("genadmin".equals(action)) {
            User user=userMapper.selectByPrimaryKey(Long.parseLong(id));
            user.setRole("user");
            userMapper.updateByPrimaryKey(user);
            Notice notice=new Notice();
            notice.setReadflag(0);
            java.text.SimpleDateFormat formatter=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date currentTime=new java.util.Date();
            notice.setGmtCreat(formatter.format(currentTime));
            notice.setMsg("你被管理员降级为网站用户");
            notice.setUserId(Long.parseLong(id));
            noticeMapper.insert(notice);
        }
        //  model.addAttribute("manager",manager);
        return "redirect:/admin/users?"+"page="+page+"&search="+search;
    }
    public List<Map<String,String>> root(String rolename){
        List<Map<String,String>> roots1=new ArrayList<>();
        WebrootExample webrootExample=new WebrootExample();
        webrootExample.createCriteria().andRolenameEqualTo(rolename).andIsdeleteEqualTo(0);
        List<Webroot> webroots=webrootMapper.selectByExample(webrootExample);
        for (int i=0;i<webroots.size();i++){
            if (webroots.get(i).getFunctionname().equals("帖子管理")){
                Map<String,String> map=new HashMap<>();
                map.put("eng","posts");
                map.put("cha","帖子管理");
                roots1.add(map);
            }else if(webroots.get(i).getFunctionname().equals("标签管理")){
                Map<String,String> map=new HashMap<>();
                map.put("eng","lables");
                map.put("cha","标签管理");
                roots1.add(map);
            }else if(webroots.get(i).getFunctionname().equals("博客介绍")){
                Map<String,String> map=new HashMap<>();
                map.put("eng","intro");
                map.put("cha","博客介绍");
                roots1.add(map);
            }else if(webroots.get(i).getFunctionname().equals("程序管理")){
                Map<String,String> map=new HashMap<>();
                map.put("eng","project");
                map.put("cha","程序管理");
                roots1.add(map);
            }else if(webroots.get(i).getFunctionname().equals("用户管理")){
                Map<String,String> map=new HashMap<>();
                map.put("eng","users");
                map.put("cha","用户管理");
                roots1.add(map);
            }else if(webroots.get(i).getFunctionname().equals("权限管理")){
                Map<String,String> map=new HashMap<>();
                map.put("eng","root");
                map.put("cha","权限管理");
                roots1.add(map);
            }else{
                Map<String,String> map=new HashMap<>();
                map.put("eng","chart");
                map.put("cha","网站视图");
                roots1.add(map);
            }
        }
        return roots1;
    }
    @GetMapping("/addroot/{action}")
    public String addroot(@PathVariable(name = "action") String action,@RequestParam(name = "functionName") String functionName, Model model, HttpServletRequest request, HttpServletResponse response) {
            WebrootExample webrootExample=new WebrootExample();
            webrootExample.createCriteria().andRolenameEqualTo(action).andFunctionnameEqualTo(functionName);
            Webroot webroot=webrootMapper.selectByExample(webrootExample).get(0);
            webroot.setIsdelete(0);
            webrootMapper.updateByPrimaryKey(webroot);
            return "redirect:/admin/root";
    }
    @GetMapping("/deleteroot/{action}")
    public String deleteroot(@PathVariable(name = "action") String action,@RequestParam(name = "functionName") String functionName, Model model, HttpServletRequest request, HttpServletResponse response) {
            WebrootExample webrootExample=new WebrootExample();
            webrootExample.createCriteria().andRolenameEqualTo(action).andFunctionnameEqualTo(functionName);
            Webroot webroot=webrootMapper.selectByExample(webrootExample).get(0);
            webroot.setIsdelete(1);
            webrootMapper.updateByPrimaryKey(webroot);
            return "redirect:/admin/root";
    }
    /*@RequestMapping("/qqLogin")
    public void qqLogin(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html;charset=utf-8");
        try {
            response.sendRedirect(new Oauth().getAuthorizeURL(request));
        } catch (QQConnectException e) {
            e.printStackTrace();
        }
    }
*/
    /**
     * 回调方法
     * @param request
     * @param response
     * @throws Exception
     */
   /* @RequestMapping("/connect")
    public void connect(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();
        try {
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);
            String accessToken   = null,
                    openID        = null;
            long tokenExpireIn = 0L;
            if (accessTokenObj.getAccessToken().equals("")) {
//                我们的网站被CSRF攻击了或者用户取消了授权
//                做一些数据统计工作
                System.out.print("没有获取到响应参数");
            } else {
                accessToken = accessTokenObj.getAccessToken();
                tokenExpireIn = accessTokenObj.getExpireIn();
                request.getSession().setAttribute("demo_access_token", accessToken);
                request.getSession().setAttribute("demo_token_expirein", String.valueOf(tokenExpireIn));

                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj =  new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();

                out.println("欢迎你，代号为 " + openID + " 的用户!");
                request.getSession().setAttribute("demo_openid", openID);



                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                out.println("<br/>");
                if (userInfoBean.getRet() == 0) {
                    out.println(userInfoBean.getNickname() + "<br/>");
                    out.println(userInfoBean.getGender() + "<br/>");

                    out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL30() + "><br/>");
                    out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL50() + "><br/>");
                    out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL100() + "><br/>");
                } else {
                    out.println("很抱歉，我们没能正确获取到您的信息，原因是： " + userInfoBean.getMsg());
                }



            }
        } catch (QQConnectException e) {
            e.printStackTrace();
        }
    }
*/
   /* @GetMapping("/introduce")
    public String introduce(@RequestParam(name = "qq") String qq,
                            @RequestParam(name = "text") String text, HttpServletRequest request, HttpServletResponse response) {
        User user=(User)request.getSession().getAttribute("user");
        String title=user.getName()+"发来通知:";
        qq=qq+"@qq.com";
        try {
            SendqqMail.send(qq,text,title);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return "introduce";
    }*/
}
