package life.majiang.community.controller;

import life.majiang.community.dto.CommentDTO;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.mapper.CollectMapper;
import life.majiang.community.mapper.CommongoodMapper;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.mapper.TuijianMapper;
import life.majiang.community.model.*;
import life.majiang.community.service.CommentService;
import life.majiang.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by codedrinker on 2019/5/21.
 */
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private CommongoodMapper commongoodMapper;

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private TuijianMapper tuijianMapper;
    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") String id, Model model, HttpServletRequest request) {
        Long questionId = null;
        try {
            questionId = Long.parseLong(id);   //输入的问题号，如果不是数字，则报错
        } catch (NumberFormatException e) {
            throw new CustomizeException(CustomizeErrorCode.INVALID_INPUT);
        }
        User user = (User) request.getSession().getAttribute("user");
        //根据userid和questionid查询是否收藏过。
        if(user!=null) {
            CollectExample collectExample = new CollectExample();
            collectExample.createCriteria().andUserEqualTo(user.getId())
                    .andQuestionEqualTo(questionId);
            List<Collect> collect = collectMapper.selectByExample(collectExample);
            long collectId = 0;
            if (collect.size() != 0) {
                collectId = collect.get(0).getId();
            }
            //返回collectid
            model.addAttribute("collectId", collectId);
            //是否被赞过
            CommongoodExample commongoodExample=new CommongoodExample();
            commongoodExample.createCriteria().andQuestionIdEqualTo(Long.valueOf(questionId)).
                    andUserIdEqualTo(user.getId());
            if(commongoodMapper.countByExample(commongoodExample)==1){
                model.addAttribute("isnotzan", 1);
            }else{
                model.addAttribute("isnotzan", 0);
            }
        }
        else{
            long collectId1=-1;
            model.addAttribute("collectId", collectId1);
            model.addAttribute("isnotzan", -1);
        }


        QuestionDTO questionDTO = questionService.getById(questionId);
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);
        List<CommentDTO> comments = commentService.listByTargetId(questionId, CommentTypeEnum.QUESTION);
        //累加阅读数
        questionService.incView(questionId);
        if(user!=null) {
        //添加推荐
        TuijianExample tuijianExample=new TuijianExample();
        tuijianExample.createCriteria().andUserIdEqualTo(user.getId());
        Tuijian tuijian=tuijianMapper.selectByExample(tuijianExample).get(0);
        String[] tags = StringUtils.split(questionDTO.getTag(), ",");
        ArrayList<String> tags1=new ArrayList<>();
        for (String tag : tags) {
            if(!tag.equals(tuijian.getTab1())&&!tag.equals(tuijian.getTab2())&&!tag.equals(tuijian.getTab3())){
                tags1.add(tag);
            }
        }
        if(tags1.size()==0){

        }else if(tags1.size()==1){
            tuijian.setTab3(tuijian.getTab2());
            tuijian.setTab2(tuijian.getTab1());
            tuijian.setTab1(tags1.get(0));
        }else if(tags1.size()==2){
            tuijian.setTab3(tuijian.getTab1());
            tuijian.setTab2(tags1.get(1));
            tuijian.setTab1(tags1.get(0));
        }else{
            tuijian.setTab3(tags1.get(2));
            tuijian.setTab2(tags1.get(1));
            tuijian.setTab1(tags1.get(0));
        }
        tuijianMapper.updateByPrimaryKey(tuijian);
        }
        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", comments);
        model.addAttribute("relatedQuestions", relatedQuestions);
        return "question";
    }
    @GetMapping("/collect/{id}")
    public String question(@PathVariable(name = "id") Long questionId, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        //增加
        Collect collect=new Collect();
        collect.setUser(user.getId());
        collect.setQuestion(questionId);
        collect.setTime(System.currentTimeMillis());
        collectMapper.insert(collect);
        return "redirect:/question/"+questionId;
    }
    @GetMapping("/discollect")
    public String question(@RequestParam(name="collectId") long collectId, @RequestParam(name="id") long questionId){
        //删除
        collectMapper.deleteByPrimaryKey(collectId);
        return "redirect:/question/"+questionId;
    }
    @ResponseBody//因为没加这个，一直错
    @RequestMapping(value = "/quszan", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> zan(@RequestBody Map<String,Object> requestMap, HttpServletRequest request,Model model) {
        String id = requestMap.get("id").toString();
        String upId = requestMap.get("upId").toString();
        User user = (User) request.getSession().getAttribute("user");

        Map<String, Object> resultMap = new HashMap<>();
        Question question=new Question();
        question=questionMapper.selectByPrimaryKey(Long.valueOf(id));
        long quscount=question.getLikeCount();

        CommongoodExample commongoodExample=new CommongoodExample();
        commongoodExample.createCriteria().andQuestionIdEqualTo(Long.valueOf(id)).
                andUserIdEqualTo(user.getId());
        if(commongoodMapper.countByExample(commongoodExample)==1){
            //已赞过
            quscount--;
            question.setLikeCount((int)quscount);
            //总评论数-1
            questionMapper.updateByPrimaryKey(question);
            //评论赞表删除
            commongoodMapper.deleteByExample(commongoodExample);
            resultMap.put("isnotzan",0);
        }else{
            quscount++;
            question.setLikeCount((int)quscount);
            //总评论数+1
            questionMapper.updateByPrimaryKey(question);
            //评论赞表添加
            Commongood commongood=new Commongood();
            commongood.setCreatGmt(System.currentTimeMillis());
            commongood.setQuestionId(Long.valueOf(id));
            commongood.setUserName(user.getName());
            commongood.setUserId(user.getId());
            commongood.setUpId(Long.valueOf(upId));
            commongood.setReadflag(0);
            commongood.setCommonId(0L);
            if(question.getTitle().length()>32){
                commongood.setContent(question.getTitle().substring(0,32)+"......");
            }else{
                commongood.setContent(question.getTitle());
            }
            commongoodMapper.insert(commongood);
            resultMap.put("isnotzan",1);
        }
        resultMap.put("quscount",quscount);
        return resultMap;
    }
    @GetMapping("/questionzan")
    public String questionzan(@RequestParam(name="questionid") String questionid,@RequestParam(name="id") String id,Model model, HttpServletRequest request) {
        Long questionId = null;
        Long Id = Long.parseLong(id);
        try {
            questionId = Long.parseLong(questionid);
        } catch (NumberFormatException e) {
            throw new CustomizeException(CustomizeErrorCode.INVALID_INPUT);
        }
        System.out.println(id+"---"+questionid);
        //变成已读
        Commongood commongood= commongoodMapper.selectByPrimaryKey(Id);
        commongood.setReadflag(1);
        commongoodMapper.updateByPrimaryKey(commongood);

       return "redirect:/question/"+questionId;
    }


}
