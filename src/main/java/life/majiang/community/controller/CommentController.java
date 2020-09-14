package life.majiang.community.controller;


import life.majiang.community.dto.CommentCreateDTO;
import life.majiang.community.dto.CommentDTO;
import life.majiang.community.dto.ResultDTO;
import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.mapper.*;
import life.majiang.community.model.*;
import life.majiang.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by codedrinker on 2019/5/30.
 */
@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private CommongoodMapper commongoodMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IntegralMapper integralMapper;
    @Autowired
    private NotificationMapper notificationMapper;

    @ResponseBody
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if (commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())) {
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());   //是上一级id。一级评论是问题，二级评论是一级评论
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        comment.setCommentCount(0);
        commentService.insert(comment,user);
        //添加通知
        Question question=new Question();
        if(comment.getType()==1){
            question=questionMapper.selectByPrimaryKey(comment.getParentId());
        }else{
            Comment comment1=commentMapper.selectByPrimaryKey(comment.getParentId());
            question=questionMapper.selectByPrimaryKey(comment1.getParentId());
        }

        Notification notification=new Notification();
        notification.setReceiver(comment.getCommentator());
        notification.setStatus(0);
        notification.setNotifier(user.getId());
        notification.setOuterid(comment.getParentId());
        notification.setType(comment.getType());
        notification.setOuterTitle(question.getTitle());
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setNotifierName(user.getName());
        notificationMapper.insert(notification);
        //加分
        user.setIntegral(user.getIntegral()+1);
        userMapper.updateByPrimaryKey(user);
        //加分纪录
        Integral integral=new Integral();
        integral.setGmtCreat(System.currentTimeMillis());
        integral.setNum(1);
        integral.setReason("评论帖子");
        integral.setUserId(user.getId());
        integral.setUserName(user.getName());
        integralMapper.insert(integral);
        return ResultDTO.okOf();
    }

    @ResponseBody
    @RequestMapping(value = "/comment/{id}", method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comments(@PathVariable(name = "id") Long id,HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }
    @ResponseBody//因为没加这个，一直错
    @RequestMapping(value = "/zan", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> zan(@RequestBody Map<String,Object> requestMap,HttpServletRequest request) {
        String id = requestMap.get("id").toString();
        String upId = requestMap.get("upId").toString();
        String qusId = requestMap.get("qusId").toString();
        User user = (User) request.getSession().getAttribute("user");
        Map<String, Object> resultMap = new HashMap<>();
        if (user == null) {
            resultMap.put("code",201);
            return resultMap;
        }
        Comment comment=new Comment();
        comment=commentMapper.selectByPrimaryKey(Long.valueOf(id));
        long comcount=comment.getLikeCount();

        CommongoodExample commongoodExample=new CommongoodExample();
        commongoodExample.createCriteria().andCommonIdEqualTo(Long.valueOf(id)).
                andUserIdEqualTo(user.getId());
        if(commongoodMapper.countByExample(commongoodExample)==1){
            comcount--;
            comment.setLikeCount(comcount);
            //总评论数-1
            commentMapper.updateByPrimaryKey(comment);
            //评论赞表删除;
            commongoodMapper.deleteByExample(commongoodExample);
        }else{
            comcount++;
            comment.setLikeCount(comcount);
            //总评论数+1
            commentMapper.updateByPrimaryKey(comment);
            //评论赞表添加
            Commongood commongood=new Commongood();
            commongood.setCreatGmt(System.currentTimeMillis());
            commongood.setCommonId(Long.valueOf(id));
            commongood.setUserName(user.getName());
            commongood.setUserId(user.getId());
            commongood.setUpId(Long.valueOf(upId));
            commongood.setReadflag(0);
            commongood.setQuestionId(Long.valueOf(qusId));
            commongood.setContent(comment.getContent());
            commongoodMapper.insert(commongood);
        }
        resultMap.put("comcount",comcount);

        resultMap.put("code",200);
        return resultMap;
    }
    @ResponseBody//因为没加这个，一直错
    @RequestMapping(value = "/delectcomment", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> delectcomment(@RequestBody Map<String,Object> requestMap,HttpServletRequest request) {
        String id = requestMap.get("id").toString();
        String questionId = requestMap.get("questionId").toString();
        Map<String, Object> resultMap = new HashMap<>();
        commentMapper.deleteByPrimaryKey(Long.valueOf(id));
        //更改回复个数
        Question question=questionMapper.selectByPrimaryKey(Long.valueOf(questionId));
        question.setCommentCount(question.getCommentCount()-1);
        questionMapper.updateByPrimaryKey(question);
        resultMap.put("code",200);
        resultMap.put("commentCount",question.getCommentCount());
        return resultMap;
    }
}
