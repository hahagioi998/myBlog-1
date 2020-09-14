package life.majiang.community.mapper;

import life.majiang.community.dto.UserQueryDTO;
import life.majiang.community.model.User;

import java.util.List;

public interface UserExtMapper {
    List<User> selectBySearch(UserQueryDTO userQueryDTO);

    Integer countBySearch(UserQueryDTO userQueryDTO);
}