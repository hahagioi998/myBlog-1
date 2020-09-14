package life.majiang.community.controller;

import com.alibaba.excel.EasyExcel;
import life.majiang.community.cache.HotTagCache;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Question;
import life.majiang.community.model.QuestionExample;
import life.majiang.community.model.User;
import life.majiang.community.model.UserExample;
import life.majiang.community.service.QuestionService;
import life.majiang.community.service.UserService;
import life.majiang.community.util.QuestionExcelLisenter;
import life.majiang.community.util.UserExcelLisenter;
import org.apache.catalina.core.ApplicationPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class ExcelController {
    @Autowired
    private HotTagCache hotTagCache;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @GetMapping("/excelchu/{action}")
    public String excelchu(@PathVariable(name = "action") String action) {
        if(action.equals("users")){
            String filename = "F:\\users.xlsx";
            UserExample userExample = new UserExample();
            userExample.createCriteria().andIdIsNotNull();
            List<User> users=userMapper.selectByExample(userExample);
            EasyExcel.write(filename, User.class).sheet("用户列表").doWrite(users);

            return "redirect:/admin/" + action;
        }else if(action.equals("posts")){
            String filename = "F:\\posts.xlsx";
            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria().andIdIsNotNull();
            List<Question> questions=questionMapper.selectByExample(questionExample);
            EasyExcel.write(filename, Question.class).sheet("文章列表").doWrite(questions);

            return "redirect:/admin/" + action;
        }
        return null;
    }
   @RequestMapping("/excelru/{action}")
   public String uploadExcel(HttpServletRequest request,@PathVariable(name = "action") String action) {
       String path = request.getServletContext().getRealPath("/");    //获取服务器地址
       Part p = null;//获取用户选择的上传文件

       if(action.equals("users")){
           try {
               p = request.getPart("userexcel");
           } catch (IOException e) {
               e.printStackTrace();
           } catch (ServletException e) {
               e.printStackTrace();
           }
           String fname2 = "";
           ApplicationPart ap = (ApplicationPart) p;
           //获取上传文件名
           String fname1 = ap.getSubmittedFileName();
           //以下代码取得文件的后缀名
           int dot = fname1.lastIndexOf(".");
           String extentname = fname1.substring(dot + 1);
           String firstname = "emp1";
           fname2 = firstname + "." + extentname;
           // 写入 web 项目根路径下的upload文件夹中
           try {
               p.write(path + fname2);
           } catch (IOException e) {
               e.printStackTrace();
           }
           //写入数据库
           try {
               request.setCharacterEncoding("utf-8");
           } catch (UnsupportedEncodingException e) {
               e.printStackTrace();
           }
           String filePath = request.getRealPath(fname2);
           System.out.println(filePath);
           EasyExcel.read(filePath,User.class,new UserExcelLisenter()).sheet().doRead();

           return "redirect:/admin/" + action;
       }else if(action.equals("posts")){
           try {
               p = request.getPart("postsexcel");
           } catch (IOException e) {
               e.printStackTrace();
           } catch (ServletException e) {
               e.printStackTrace();
           }
           String fname2 = "";
           ApplicationPart ap = (ApplicationPart) p;
           //获取上传文件名
           String fname1 = ap.getSubmittedFileName();
           //以下代码取得文件的后缀名
           int dot = fname1.lastIndexOf(".");
           String extentname = fname1.substring(dot + 1);
           String firstname = "emp1";
           fname2 = firstname + "." + extentname;
           // 写入 web 项目根路径下的upload文件夹中
           try {
               p.write(path + fname2);
           } catch (IOException e) {
               e.printStackTrace();
           }
           //写入数据库
           try {
               request.setCharacterEncoding("utf-8");
           } catch (UnsupportedEncodingException e) {
               e.printStackTrace();
           }
           String filePath = request.getRealPath(fname2);
           System.out.println(filePath);

           EasyExcel.read(filePath,Question.class,new QuestionExcelLisenter()).sheet().doRead();
       }
       return null;
   }
}