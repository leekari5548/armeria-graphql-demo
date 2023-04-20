package cn.leekari.sql;

import cn.leekari.config.JDBCClientException;
import cn.leekari.config.JDBCHikariCPClient;
import cn.leekari.entity.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    JDBCHikariCPClient jdbcClient = new JDBCHikariCPClient();

    public UserMapper() {
        jdbcClient.connect();
    }

    public List<User> users() {
        List<User> users = new ArrayList<>();
        String sql = "select id,name,gender,birthday from user";
        try (Connection connection = jdbcClient.getConnection()){
            try (ResultSet resultSet = jdbcClient.executeQuery(connection, sql)){
                while (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getLong("id"));
                    user.setName(resultSet.getString("name"));
                    user.setGender(resultSet.getInt("gender"));
                    user.setBirthday(resultSet.getObject("birthday", OffsetDateTime.class));
                    users.add(user);
                }
                System.err.println(users);
            }catch (JDBCClientException | SQLException e) {
                e.printStackTrace();
            }
        }catch (JDBCClientException | SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User user(Long userId) {
        String sql = "select id,name,gender,birthday from user where id = ?";
        User user = new User();
        try (Connection connection = jdbcClient.getConnection()){
            try (ResultSet resultSet = jdbcClient.executeQuery(connection, sql, userId)){
                while (resultSet.next()) {
                    user.setId(resultSet.getLong("id"));
                    user.setName(resultSet.getString("name"));
                    user.setGender(resultSet.getInt("gender"));
                    user.setBirthday(resultSet.getObject("birthday", OffsetDateTime.class));
                }
            }catch (JDBCClientException | SQLException e) {
                e.printStackTrace();
            }
        }catch (JDBCClientException | SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void main(String[] args) {
        UserMapper userMapper = new UserMapper();
        System.err.println(userMapper.user(1L));
    }
}
