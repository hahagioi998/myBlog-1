package life.majiang.community.service;

import life.majiang.community.dto.AttentionDTO;
import life.majiang.community.dto.NotificationDTO;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.enums.NotificationStatusEnum;
import life.majiang.community.enums.NotificationTypeEnum;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.exception.CustomizeException;
import life.majiang.community.mapper.AttentionMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * Created by codedrinker on 2019/6/14.
 */
@Service
public class AttentionService {
    @Autowired
    private AttentionMapper attentionMapper;
    @Autowired
    private UserMapper userMapper;
    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO<User> paginationDTO = new PaginationDTO<>();
        Integer totalPage;
        AttentionExample attentionExample = new AttentionExample();
        attentionExample.createCriteria()
                .andAttentionUserEqualTo(userId);
        Integer totalCount = (int) attentionMapper.countByExample(attentionExample);
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
        AttentionExample example = new AttentionExample();
        example.createCriteria()
                .andAttentionUserEqualTo(userId);
        example.setOrderByClause("id desc");
        List<Attention> attentions = attentionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));
        if (attentions.size() == 0) {
            return paginationDTO;
        }
        List<User> users=new ArrayList<>();
        for (Attention attention : attentions) {
            User user=userMapper.selectByPrimaryKey(attention.getAttentionedUser());
            users.add(user);
        }
        paginationDTO.setData(users);
        return paginationDTO;
    }
    public List<AttentionDTO> list(Long userId) {
        AttentionExample attentionExample = new AttentionExample();
        attentionExample.createCriteria()
                .andAttentionUserEqualTo(userId);
        attentionExample.setOrderByClause("id desc");
        List<Attention> attentions = attentionMapper.selectByExample(attentionExample);
        List<AttentionDTO> attentionDTOS=new ArrayList<>();
        for (Attention attention : attentions) {
            User user=userMapper.selectByPrimaryKey(attention.getAttentionedUser());
            AttentionDTO attentionDTO=new AttentionDTO();
            attentionDTO.setId(attention.getId());
            attentionDTO.setUser(user);
            attentionDTOS.add(attentionDTO);
        }
        return attentionDTOS;
    }
    public PaginationDTO list1(Long userId, Integer page, Integer size) {
        PaginationDTO<User> paginationDTO = new PaginationDTO<>();
        Integer totalPage;
        AttentionExample attentionExample = new AttentionExample();
        attentionExample.createCriteria()
                .andAttentionedUserEqualTo(userId);
        Integer totalCount = (int) attentionMapper.countByExample(attentionExample);
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
        AttentionExample example = new AttentionExample();
        example.createCriteria()
                .andAttentionedUserEqualTo(userId);
        example.setOrderByClause("id desc");
        List<Attention> attentions = attentionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));
        if (attentions.size() == 0) {
            return paginationDTO;
        }
        List<User> users=new ArrayList<>();
        for (Attention attention : attentions) {
            User user=userMapper.selectByPrimaryKey(attention.getAttentionUser());
            users.add(user);
        }
        paginationDTO.setData(users);
        return paginationDTO;
    }
}
