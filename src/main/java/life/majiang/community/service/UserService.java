package life.majiang.community.service;

import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.UserQueryDTO;
import life.majiang.community.mapper.NoticeMapper;
import life.majiang.community.mapper.UserExtMapper;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.Notice;
import life.majiang.community.model.User;
import life.majiang.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by codedrinker on 2019/5/23.
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NoticeMapper noticeMapper;
    @Autowired
    private UserExtMapper userExtMapper;
    public void createOrUpdate(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);

        if (users.size() == 0) {
            // 插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        } else {
            //更新
            User dbUser = users.get(0);
            User updateUser = new User();
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());

            UserExample example = new UserExample();
            example.createCriteria()
                    .andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(updateUser, example);
        }
    }
    public List<User> checkWebUserExist(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andPasswordEqualTo(user.getPassword())
                .andPhoneEqualTo(user.getPhone());
        List<User> users= userMapper.selectByExample(userExample);
        return users;
    }
    public List<User> checkWebUserExistByPhone(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andPhoneEqualTo(user.getPhone());
        List<User> users= userMapper.selectByExample(userExample);
        return users;
    }

    public PaginationDTO list(Integer page, Integer size, String search) {
        PaginationDTO paginationDTO=new PaginationDTO();
        Integer totalPage;
        UserQueryDTO userQueryDTO=new UserQueryDTO();
        userQueryDTO.setSearch(search);
        Integer totalCount=userExtMapper.countBySearch(userQueryDTO);
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
            userQueryDTO.setSize(size);
            userQueryDTO.setPage(offset);
            List<User> users = userExtMapper.selectBySearch(userQueryDTO);

            paginationDTO.setData(users);

            return paginationDTO;
        }
    }

    public void Send(Long valueOf, String content, String fb_time) {
        //写数据到表
        Notice notice=new Notice();
        notice.setMsg(content);
        notice.setGmtCreat(fb_time);
        notice.setUserId(valueOf);
        noticeMapper.insert(notice);
    }
}
