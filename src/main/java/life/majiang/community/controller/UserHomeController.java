package life.majiang.community.controller;

import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.mapper.*;
import life.majiang.community.model.*;
import life.majiang.community.service.CollectService;
import life.majiang.community.service.NotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class UserHomeController {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AttentionMapper attentionMapper;
    @Autowired
    private IntegralMapper integralMapper;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CollectService collectService;
    @Autowired
    private TuijianMapper tuijianMapper;
    @GetMapping("userhome/{action}")
    public String userhome(HttpServletRequest request, Model model, @PathVariable(name = "action") String action,@RequestParam(name="search",required = false) String search,@RequestParam(name = "page", defaultValue = "1") Integer page,
                           @RequestParam(name = "size", defaultValue = "5") Integer size){
        User user = (User) request.getSession().getAttribute("user");
        //发帖数
        QuestionExample questionExample=new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(user.getId());
        Integer totalCount=(int)questionMapper.countByExample(questionExample);
        //粉丝数
        AttentionExample attentionExample=new AttentionExample();
        attentionExample.createCriteria().andAttentionedUserEqualTo(user.getId());
        Integer attentionmeCount=(int)attentionMapper.countByExample(attentionExample);
        //关注数
        AttentionExample attentionExample1=new AttentionExample();
        attentionExample1.createCriteria().andAttentionUserEqualTo(user.getId());
        List<Attention> attentions= attentionMapper.selectByExample(attentionExample1);
        Integer meattentionCount=attentions.size();
        //积分纪录
        IntegralExample integralExample=new IntegralExample();
        integralExample.createCriteria().andUserIdEqualTo(user.getId());
        integralExample.setOrderByClause("gmt_creat desc");
        List<Integral> integrals=integralMapper.selectByExample(integralExample);
        List<Integral> integrals1=new ArrayList<>();
        if(integrals.size()>4){
            integrals1=integrals.subList(0,3);
        }else{
            integrals1=integrals;
        }
        //推荐
        TuijianExample tuijianExample=new TuijianExample();
        tuijianExample.createCriteria().andUserIdEqualTo(user.getId());
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
        model.addAttribute("search",search);
        model.addAttribute("integrals",integrals);
        model.addAttribute("integrals1",integrals1);
        model.addAttribute("attentionmeCount",attentionmeCount);
        model.addAttribute("meattentionCount",meattentionCount);
        model.addAttribute("totalCount",totalCount);
        //得到关注的所有人的id
        //循环生成questions
        //再按时间顺序排序
        if ("home".equals(action)) {
            List<Question> questions=new ArrayList<>();
            List<QuestionDTO> questionDTOList = new ArrayList<>();
            for(Attention attention:attentions){
                //Long attentioneuUserId=attention.getAttentionedUser();
                QuestionExample questionExample1=new QuestionExample();
                questionExample1.createCriteria().andCreatorEqualTo(attention.getAttentionedUser());
                List<Question> questions1=new ArrayList<>();
                questions1=questionMapper.selectByExample(questionExample1);
                questions.addAll(questions1);
            }
            Collections.sort(questions, new Comparator<Question>() {
                @Override
                public int compare(Question question1, Question question2) {
                    Long id1 = question1.getId();
                    Long id2 = question2.getId();
                    //可以按Question对象的其他属性排序，只要属性支持compareTo方法
                    return id2.compareTo(id1);
                }
            });
            //分页处理
            Integer totalPage;
            int totalCount1=questions.size();
            if (totalCount1 % size == 0) {
                totalPage = totalCount1 / size;   //整除
            } else {
                totalPage = totalCount1 / size + 1;  //不能整除，就+1
            }
            if (page < 1) {
                page = 1;
            }
            if (page > totalPage) {
                page = totalPage;
            }
            PaginationDTO paginationDTO = new PaginationDTO();
            paginationDTO.setPagination(totalPage, page);
            if(page==0){
                questionDTOList=null;
            }else{
                Integer offset = size * (page - 1);  //跨度
                List<Question> questions1=null;
                if(totalCount1>offset&&totalCount1<(offset+5)){
                    questions1= questions.subList(0+offset,totalCount1);
                }else{
                    questions1= questions.subList(0+offset,offset+5);
                }
                for (Question question : questions1) {
                    User user1 = userMapper.selectByPrimaryKey(question.getCreator());
                    QuestionDTO questionDTO = new QuestionDTO();
                    if(question.getTitle().length()>85){
                        question.setTitle(question.getTitle().substring(0,85)+"......");
                    }
                    BeanUtils.copyProperties(question, questionDTO);
                    questionDTO.setUser(user1);
                    questionDTOList.add(questionDTO);
                }
            }

            paginationDTO.setData(questionDTOList);
            model.addAttribute("paginationDTO",paginationDTO);
            model.addAttribute("section", "home");
            model.addAttribute("sectionName", "我的主页");
            return  "userhome";

        } else if ("notification".equals(action)) {
            PaginationDTO paginationDTO = notificationService.list(user.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);
            model.addAttribute("section", "notification");
            model.addAttribute("sectionName", "我的通知");
        }else if ("collect".equals(action)) {
            PaginationDTO paginationDTO = collectService.list(user.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);
            model.addAttribute("section", "collect");
            model.addAttribute("sectionName", "我的收藏");
        }


        return  "userhome";
    }
}
