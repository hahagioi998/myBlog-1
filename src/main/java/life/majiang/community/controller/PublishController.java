package life.majiang.community.controller;

import life.majiang.community.cache.TagCache;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.mapper.IntegralMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Integral;
import life.majiang.community.model.Question;
import life.majiang.community.model.User;
import life.majiang.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by codedrinker on 2019/5/2.
 */
@Controller
public class PublishController {
    @Autowired
    private TagCache tagCache;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IntegralMapper integralMapper;
   @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id") Long id,
                       Model model) {
        QuestionDTO question=questionService.getById(id);
        model.addAttribute("title", question.getTitle());
        model.addAttribute("description", question.getDescription());
        model.addAttribute("tag", question.getTag());
        model.addAttribute("id", question.getId());
        model.addAttribute("tags", tagCache.get());
        return "publish";
    }
   @GetMapping("/publish")
    public String publish(Model model) {
        String section2="publish";
        model.addAttribute("section2",section2);
        model.addAttribute("tags", tagCache.get());
        return "publish";
    }
    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "id", required = false) Long id,
            HttpServletRequest request,
            Model model) {
        String section2="publish";
        model.addAttribute("section2",section2);
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", tagCache.get());
       /* if (StringUtils.isBlank(title)) {*/
        if (title.equals("")||title.trim().equals("")){  //去掉空格
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }
      /*  if (StringUtils.isBlank(description)) {*/
        if (description.equals("")||description.trim().equals("")){
            model.addAttribute("error", "问题补充不能为空");
            return "publish";
        }
        /*if (StringUtils.isBlank(tag)) {*/
        if (tag.equals("")||tag.trim().equals("")){
            model.addAttribute("error", "标签不能为空");
            return "publish";
        }
        String invalid = tagCache.filterInvalid(tag);
        if (StringUtils.isNotBlank(invalid)) {
            model.addAttribute("error", "输入非法标签:" + invalid);
            return "publish";
        }
        User user = (User) request.getSession().getAttribute("user");
       /* if (user == null) {
            model.addAttribute("error", "用户未登录");
            return "publish";
        }*/
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setId(id);
        questionService.createOrUpdate(question);
        //个人加分
        user.setIntegral(user.getIntegral()+100);
        userMapper.updateByPrimaryKey(user);
        //加分纪录
        Integral integral=new Integral();
        integral.setGmtCreat(System.currentTimeMillis());
        integral.setNum(100);
        integral.setReason("发表帖子");
        integral.setUserId(user.getId());
        integral.setUserName(user.getName());
        integralMapper.insert(integral);
        return "redirect:/";

    }
}
