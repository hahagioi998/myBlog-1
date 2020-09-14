package life.majiang.community.service;

import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.enums.NotificationStatusEnum;
import life.majiang.community.mapper.*;
import life.majiang.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by codedrinker on 2019/5/7.
 */
@Service
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommongoodMapper commongoodMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestiongoodMapper questiongoodMapper;
    /*public PaginationDTO list(String search, String tag, Integer page, Integer size) {
        if (StringUtils.isNotBlank(search)) {
            String[] tags = StringUtils.split(search, " ");
            search = Arrays
                    .stream(tags)
                    .filter(StringUtils::isNotBlank)
                    .map(t -> t.replace("+", "").replace("*", "").replace("?", ""))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining("|"));
        }
        PaginationDTO paginationDTO=new PaginationDTO();

        Integer totalPage;
        QuestionQueryDTO questionQueryDTO=new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        questionQueryDTO.setTag(tag);
        Integer totalCount=questionExtMapper.countBySearch(questionQueryDTO);
        if(totalCount==0){

            return paginationDTO;
        }else {
            if (totalCount % size == 0) {
                totalPage = totalCount / size;
            } else {
                totalPage = totalCount / size + 1;
            }
            if (page < 1) {
                page = 1;
            }
            if (page > totalPage) {
                page = totalPage;
            }
            paginationDTO.setPagination(totalPage, page);
            Integer offset = size * (page - 1);
            QuestionExample questionExample = new QuestionExample();
            questionExample.setOrderByClause("gmt_create desc");
            questionQueryDTO.setSize(size);
            questionQueryDTO.setPage(offset);
            List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);
            List<QuestionDTO> questionDTOList = new ArrayList<>();
            for (Question question : questions) {
                User user = userMapper.selectByPrimaryKey(question.getCreator());
                QuestionDTO questionDTO = new QuestionDTO();
                BeanUtils.copyProperties(question, questionDTO);//快速把前面的属性拷贝到后面
                questionDTO.setUser(user);
                questionDTOList.add(questionDTO);
            }
            paginationDTO.setData(questionDTOList);

            return paginationDTO;
        }
    }*/
    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        NoticeExample noticeExample=new NoticeExample();
        noticeExample.createCriteria().andUserIdEqualTo(userId);
        Integer totalCount=(int)noticeMapper.countByExample(noticeExample);
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
       paginationDTO.setPagination(totalPage, page);

        //size*(page-1)
        Integer offset = size * (page - 1);

        NoticeExample example = new NoticeExample();
        example.createCriteria()
                .andUserIdEqualTo(userId);
        example.setOrderByClause("gmt_creat desc,readflag asc");   //两个条件查询，如果把两个条件换个位置，则不行
        List<Notice> notices= noticeMapper.selectByExampleWithRowbounds(example,new RowBounds(offset,size));
        for(Notice notice:notices){
            if(notice.getMsg().length()>28){
                notice.setMsg(notice.getMsg().substring(0,28)+"...");
            }
        }
        paginationDTO.setData(notices);
        return paginationDTO;
    }
    public PaginationDTO zanlist(Long userId, Integer page, Integer size) {
        PaginationDTO<Commongood> paginationDTO = new PaginationDTO<>();
        Integer totalPage;
        //根据id查点赞表
        CommongoodExample commongoodExample=new CommongoodExample();
        commongoodExample.createCriteria().andUpIdEqualTo(userId);
        List<Commongood> commongoods=commongoodMapper.selectByExample(commongoodExample);
        Integer totalCount = commongoods.size();
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage, page);
        //size*(page-1)
        Integer offset = size * (page - 1);
        List<Commongood> commongoods1=commongoodMapper.selectByExampleWithRowbounds(commongoodExample,new RowBounds(offset,size));

        paginationDTO.setData(commongoods1);
        return paginationDTO;
    }
    //用户未读通知数
    public int unreadNoticeCount(Long userId) {
        NoticeExample noticeExample = new NoticeExample();
        noticeExample.createCriteria()
                .andUserIdEqualTo(userId)
                .andReadflagEqualTo(0);
        Long i=noticeMapper.countByExample(noticeExample);
        long j=i.longValue();
        return (int)j;
    }
    //用户未读通知数
    public int unreadZanCount(Long userId) {
        CommongoodExample  commongoodExample = new CommongoodExample();
        commongoodExample.createCriteria()
                .andUpIdEqualTo(userId)
                .andReadflagEqualTo(0);
        Long i= commongoodMapper.countByExample(commongoodExample);
        long j=i.longValue();
        return (int)j;
    }
    //用户未读通知数
   /* public int unreadFenCount(Long userId) {
        CommongoodExample  commongoodExample = new CommongoodExample();
        commongoodExample.createCriteria()
                .andUserIdEqualTo(userId)
                .andReadflagEqualTo(0);
        Long i= commongoodMapper.countByExample(commongoodExample);
        long j=i.longValue();
        return (int)j;
    }*/
}
