package life.majiang.community.service;

import life.majiang.community.dto.CollectDTO;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.mapper.CollectMapper;
import life.majiang.community.mapper.QuestionMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Collect;
import life.majiang.community.model.CollectExample;
import life.majiang.community.model.Question;
import life.majiang.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by codedrinker on 2019/5/23.
 */
@Service
public class CollectService {
    @Autowired
    private CollectMapper collectMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        //与上面不同的是多了一个参数，需要以恶搞userid。
        CollectExample collectExample=new CollectExample();
        collectExample.createCriteria().andUserEqualTo(userId);
        Integer totalCount=(int)collectMapper.countByExample(collectExample);
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
        CollectExample example = new CollectExample();
        example.createCriteria()
                .andUserEqualTo(userId);
        List<Collect> collects= collectMapper.selectByExampleWithRowbounds(example,new RowBounds(offset,size));

        List<CollectDTO> collectDTOList = new ArrayList<>();
        for (Collect collect : collects) {
            Question question=questionMapper.selectByPrimaryKey(collect.getQuestion());
            //作者
            if(question!=null){
                User user = userMapper.selectByPrimaryKey(question.getCreator());
                CollectDTO collectDTO = new CollectDTO();
                collectDTO.setId(collect.getId());
                collectDTO.setTime(collect.getTime());
                collectDTO.setUser(user);
                collectDTO.setQuestion(question);
                collectDTOList.add(collectDTO);
            }
        }
        paginationDTO.setData(collectDTOList);
        return paginationDTO;
    }
}
