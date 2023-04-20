package cn.leekari.resolver;

import cn.leekari.entity.User;
import cn.leekari.sql.UserMapper;
import graphql.kickstart.tools.GraphQLQueryResolver;

public class UserQuery implements GraphQLQueryResolver {

    public User user(Long id) {
        UserMapper userMapper = new UserMapper();
        return userMapper.user(id);
    }
}
