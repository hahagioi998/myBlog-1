package life.majiang.community.controller;

import life.majiang.community.cache.HotTagCache;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.mapper.AttentionMapper;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.mapper.TuijianMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.*;
import life.majiang.community.service.AttentionService;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OtherUserHomeController {
    @Autowired
    private HotTagCache hotTagCache;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AttentionMapper attentionMapper;
    @Autowired
    private AttentionService attentionService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private TuijianMapper tuijianMapper;
    @GetMapping("/otheruserhome")//未用
    public String index(HttpServletRequest request, Model model, @RequestParam(name="page",defaultValue="1") Integer page, @RequestParam(name="size",defaultValue="5") Integer size,@RequestParam(name="search",required = false) String search,@RequestParam(name="otherUserId",required = false) long otherUserId,@RequestParam(name="tag",required = false) String tag){
        PaginationDTO paginationDTO=questionService.list(otherUserId,page,size);
        ArrayList<User> userList = new ArrayList<User>();
        List<String> tags=hotTagCache.getHots();

        User otherUser=userMapper.selectByPrimaryKey(otherUserId);
        User user = (User) request.getSession().getAttribute("user");
        QuestionExample questionExample=new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(otherUser.getId());
        Integer totalCount=(int)questionMapper.countByExample(questionExample);

        AttentionExample attentionExample=new AttentionExample();
        attentionExample.createCriteria().andAttentionedUserEqualTo(otherUserId)
                .andAttentionUserEqualTo(user.getId());
        List<Attention> attention=attentionMapper.selectByExample(attentionExample);
        long attentionId=0;
        if(attention.size()!=0){
            attentionId=attention.get(0).getId();
        }
        AttentionExample attentionExample2=new AttentionExample();
        attentionExample2.createCriteria().andAttentionedUserEqualTo(otherUserId);
        Integer attentionheCount=(int)attentionMapper.countByExample(attentionExample2);

        AttentionExample attentionExample1=new AttentionExample();
        attentionExample1.createCriteria().andAttentionUserEqualTo(otherUserId);
        Integer heattentionCount=(int)attentionMapper.countByExample(attentionExample1);

        model.addAttribute("pagination",paginationDTO);
        model.addAttribute("search",search);
        model.addAttribute("tag",tag);
        model.addAttribute("tags",tags);

        model.addAttribute("attentionheCount",attentionheCount);
        model.addAttribute("heattentionCount",heattentionCount);
        model.addAttribute("attentionId",attentionId);
        model.addAttribute("otherUser",otherUser);
        model.addAttribute("totalCount",totalCount);
        return "otheruserhome";
    }
    @GetMapping("/otheruserhome/{action}/{otherUserId}")
    public String otheruserhome(@PathVariable(name = "action") String action,@PathVariable(name = "otherUserId") Long otherUserId, Model model, @RequestParam(name = "page", defaultValue = "1") Integer page,
                                @RequestParam(name = "size", defaultValue = "5") Integer size, HttpServletRequest request){
        User otherUser=userMapper.selectByPrimaryKey(otherUserId);
        User user = (User) request.getSession().getAttribute("user");
        long attentionId=0;
        if(user==null){
            attentionId=-1;
        }
        else{
            AttentionExample attentionExample=new AttentionExample();
            attentionExample.createCriteria().andAttentionedUserEqualTo(otherUserId)
                    .andAttentionUserEqualTo(user.getId());
            List<Attention> attention=attentionMapper.selectByExample(attentionExample);
            if(attention.size()!=0){
                attentionId=attention.get(0).getId();
            }
        }
        AttentionExample attentionExample2=new AttentionExample();
        attentionExample2.createCriteria().andAttentionedUserEqualTo(otherUserId);
        Integer attentionheCount=(int)attentionMapper.countByExample(attentionExample2);
        AttentionExample attentionExample1=new AttentionExample();
        attentionExample1.createCriteria().andAttentionUserEqualTo(otherUserId);
        Integer heattentionCount=(int)attentionMapper.countByExample(attentionExample1);
        QuestionExample questionExample=new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(otherUserId);
        Integer totalCount=(int)questionMapper.countByExample(questionExample);

        //推荐
        TuijianExample tuijianExample=new TuijianExample();
        tuijianExample.createCriteria().andUserIdEqualTo(otherUser.getId());
        Tuijian tuijian=tuijianMapper.selectByExample(tuijianExample).get(0);
        //三个tag，根据每个tag去搜索两个question,再根据tag去搜类，再根据tag类搜一个出原tag以外的一个tag
        String tag1=tuijian.getTab1();
        String tag2=tuijian.getTab2();
        String tag3=tuijian.getTab3();

        QuestionExample questionExample4=new QuestionExample();
        questionExample4.createCriteria().andTagLike(tag1);
        List<Question> questionstuijian=questionMapper.selectByExample(questionExample4);
        QuestionExample questionExample2=new QuestionExample();
        questionExample2.createCriteria().andTagLike(tag2);
        List<Question> questions2=questionMapper.selectByExample(questionExample2);
        QuestionExample questionExample3=new QuestionExample();
        questionExample3.createCriteria().andTagLike(tag3);
        List<Question> questions3=questionMapper.selectByExample(questionExample3);
        questionstuijian.addAll(questions2);
        questionstuijian.addAll(questions3);
        if(questionstuijian.size()>9){
            questionstuijian=questionstuijian.subList(0,9);
        }
        ///////
        model.addAttribute("questionstuijian",questionstuijian);
        if ("tie".equals(action)) {
            PaginationDTO paginationDTO = questionService.list(otherUser.getId(), page, size);
            model.addAttribute("section1", "tie");
            model.addAttribute("pagination", paginationDTO);
        }else if("attention".equals(action)){//关注
            PaginationDTO paginationDTO = attentionService.list(otherUser.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);
            model.addAttribute("section1", "attention");
        }else if("attentioned".equals(action)){//粉丝
            PaginationDTO paginationDTO = attentionService.list1(otherUser.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);
            model.addAttribute("section1", "attentioned");
        }
        model.addAttribute("attentionheCount",attentionheCount);
        model.addAttribute("heattentionCount",heattentionCount);
        model.addAttribute("attentionId",attentionId);
        model.addAttribute("otherUser",otherUser);
        model.addAttribute("totalCount",totalCount);
        return  "otheruserhome";
    }
    @GetMapping("/attention/{otherUserId}")
    public String otheruserhome(@PathVariable(name = "otherUserId") Long otherUserId, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        //增加
        Attention attention=new Attention();
        attention.setAttentionUser(user.getId());
        attention.setAttentionedUser(otherUserId);
         attentionMapper.insert(attention);

        return "redirect:/otheruserhome/tie/"+otherUserId;
    }
    @GetMapping("/disattention")
    public String otheruserhome(@RequestParam(name="attentionId") long attentionId,@RequestParam(name="otherUserId") long otherUserId){
        //删除
        attentionMapper.deleteByPrimaryKey(attentionId);
        return "redirect:/otheruserhome/tie/"+otherUserId;
    }

}
