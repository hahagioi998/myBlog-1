package life.majiang.community.controller;

import life.majiang.community.cache.TagCache;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.mapper.*;
import life.majiang.community.model.Echarts;
import life.majiang.community.model.Tagclass;
import life.majiang.community.model.Tags;
import life.majiang.community.model.TagsExample;
import life.majiang.community.service.QuestionService;
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
import java.util.List;

/**
 * Created by codedrinker on 2019/5/21.
 */
@Controller
public class DelectController {
    @Autowired
    private TagCache tagCache;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TagsMapper tagsMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private NoticeMapper noticeMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @GetMapping("/delect/{action}/{id}")
    public String delect(@RequestParam(name="page", defaultValue="1") Integer page,
                         @RequestParam(name="search",required = false,defaultValue="") String search,
                         @PathVariable(name = "action") String action, @PathVariable(name = "id") String id,
                         Model model, HttpServletRequest request, HttpServletResponse response) {
        if ("posts".equals(action)) {
            Long questionId = null;
            try {
                questionId = Long.parseLong(id);
            } catch (NumberFormatException e) {
                throw new CustomizeException(CustomizeErrorCode.INVALID_INPUT);
            }
            questionMapper.deleteByPrimaryKey(questionId);
            if(search.equals("null")){
                search="";
            }
            return "redirect:/admin/posts?"+"page="+page+"&search="+search;
        } else if ("lables".equals(action)) {
             tagsMapper.deleteByPrimaryKey(Long.parseLong(id));
              return "redirect:/lable";
        }else if ("intro".equals(action)) {
           // manager="intro";
            return "question";
        }
        else if ("project".equals(action)) {
            projectMapper.deleteByPrimaryKey(Long.parseLong(id));
           // manager="project";
            return "redirect:/admin/project";
        }
        else if ("users".equals(action)) {
            Long userId = null;
            try {
                userId = Long.parseLong(id);
            } catch (NumberFormatException e) {
                throw new CustomizeException(CustomizeErrorCode.INVALID_INPUT);
            }
            userMapper.deleteByPrimaryKey(userId);
            if(search.equals("null")){
                search="";
            }
            return "redirect:/admin/users?"+"page="+page+"&search="+search;
        }
        else if ("chart".equals(action)) {

           // manager="chart";
            return "question";
        }
      //  model.addAttribute("manager",manager);
        return null;
    }
    @GetMapping("/delectquestion/{action}/{id}")
    public String delectquestion(@PathVariable(name = "action") String action,@PathVariable(name = "id") String id,@RequestParam(name="page", defaultValue="1") Integer page){
        Long questionId = null;
        try {
            questionId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new CustomizeException(CustomizeErrorCode.INVALID_INPUT);
        }
        questionMapper.deleteByPrimaryKey(questionId);
        if ("index".equals(action)) {
            return "redirect:/";
        }else if("profile".equals(action)){
            return "redirect:/profile/questions?page="+page;
        }
        return "redirect:/";
    }
    @GetMapping("/delectreplie/{action}/{id}")
    public String delectreplie(@PathVariable(name = "action") String action,@PathVariable(name = "id") String id,@RequestParam(name="page", defaultValue="1") Integer page){
        Long replieId = null;
        try {
            replieId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new CustomizeException(CustomizeErrorCode.INVALID_INPUT);
        }
        notificationMapper.deleteByPrimaryKey(replieId);
        if ("index".equals(action)) {   //未写
            return "redirect:/";
        }else if("profile".equals(action)){
            return "redirect:/profile/replies?page="+page;
        }
        return "redirect:/";
    }
    @GetMapping("/delectnotice/{action}/{id}")
    public String delectnotice(@PathVariable(name = "action") String action,@PathVariable(name = "id") String id,@RequestParam(name="page", defaultValue="1") Integer page){
        Long noticeId = null;
        try {
            noticeId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new CustomizeException(CustomizeErrorCode.INVALID_INPUT);
        }
        noticeMapper.deleteByPrimaryKey(noticeId);
        if ("index".equals(action)) {   //未写
            return "redirect:/";
        }else if("profile".equals(action)){
            return "redirect:/profile/notice?page="+page;
        }
        return "redirect:/";
    }

    @GetMapping("/lable")
    public String delectlable(Model model){

       List<Tagclass> tagclass = new ArrayList<>();
     //   tagCache.get();
       TagsExample tagsExample=new TagsExample();
        tagsExample.createCriteria().andIdIsNotNull();
        List<Tags> tags=tagsMapper.selectByExample(tagsExample);//最好是读取数据库中的类个数，循环生成tag
        List<Echarts> tag1=new ArrayList<>();
        List<Echarts> tag2=new ArrayList<>();
        List<Echarts> tag3=new ArrayList<>();
        List<Echarts> tag4=new ArrayList<>();
        List<Echarts> tag5=new ArrayList<>();
        List<Echarts> tag6=new ArrayList<>();
        Tagclass program = new Tagclass();
        Tagclass framework = new Tagclass();
        Tagclass server = new Tagclass(); Tagclass db = new Tagclass();
        Tagclass tool = new Tagclass(); Tagclass other = new Tagclass();

        for(int i=0;i<tags.size();i++){
            if(tags.get(i).getCla().equals("开发语言")){
                program.setCategoryName("开发语言");
                Echarts echarts1=new Echarts();
                echarts1.setName(tags.get(i).getName());
                echarts1.setValue(tags.get(i).getCount());
                echarts1.setId(tags.get(i).getId());
                tag1.add(echarts1);
            }
            if(tags.get(i).getCla().equals("平台框架")){
                framework.setCategoryName("平台框架");
                Echarts echarts2=new Echarts();
                echarts2.setName(tags.get(i).getName());
                echarts2.setValue(tags.get(i).getCount());
                echarts2.setId(tags.get(i).getId());
                tag2.add(echarts2);
            }
            if(tags.get(i).getCla().equals("服务器")){
                server.setCategoryName("服务器");
                Echarts echarts3=new Echarts();
                echarts3.setName(tags.get(i).getName());
                echarts3.setValue(tags.get(i).getCount());
                echarts3.setId(tags.get(i).getId());
                tag3.add(echarts3);
            }
            if(tags.get(i).getCla().equals("数据库")){
                db.setCategoryName("数据库");
                Echarts echarts4=new Echarts();
                echarts4.setName(tags.get(i).getName());
                echarts4.setValue(tags.get(i).getCount());
                echarts4.setId(tags.get(i).getId());
                tag4.add(echarts4);
            }
            if(tags.get(i).getCla().equals("开发工具")){
                tool.setCategoryName("开发工具");
                Echarts echarts5=new Echarts();
                echarts5.setName(tags.get(i).getName());
                echarts5.setValue(tags.get(i).getCount());
                echarts5.setId(tags.get(i).getId());
                tag5.add(echarts5);
            }
            if(tags.get(i).getCla().equals("其它")){
                other.setCategoryName("其它");
                Echarts echarts6=new Echarts();
                echarts6.setName(tags.get(i).getName());
                echarts6.setValue(tags.get(i).getCount());
                echarts6.setId(tags.get(i).getId());
                tag6.add(echarts6);
            }
        }
        program.setTags(tag1);
        framework.setTags(tag2);
        server.setTags(tag3);
        db.setTags(tag4);
        tool.setTags(tag5);
        other.setTags(tag6);
        tagclass.add(program);
        tagclass.add(framework);
        tagclass.add(server);
        tagclass.add(db);
        tagclass.add(tool);
        tagclass.add(other);

        model.addAttribute("tagclass",tagclass);
        return "lables";
    }
    @PostMapping("/addlable")
    public String addlable(@RequestParam(name = "cla") String cla,@RequestParam(name = "name") String name){
        Tags tag=new Tags();
        tag.setCla(cla);
        tag.setCount(0);
        tag.setGmtCreate(System.currentTimeMillis());
        tag.setName(name);
       tagsMapper.insert(tag);
       return "redirect:/lable";
    }
}
