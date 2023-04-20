/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.leekari.config;

import cn.leekari.entity.User;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC Client uses HikariCP connection management lib to execute SQL.
 */
public class JDBCHikariCPClient implements Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(JDBCHikariCPClient.class);

    private final HikariConfig hikariConfig;
    private HikariDataSource dataSource;

    public JDBCHikariCPClient() {
        hikariConfig = new HikariConfig();
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("root");
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false");
    }

    @Override
    public void connect() {
        dataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public void shutdown() {
        dataSource.close();
    }

    /**
     * Default getConnection is set in auto-commit.
     */
    public Connection getConnection() throws JDBCClientException {
        return getConnection(true);
    }

    public Connection getConnection(boolean autoCommit) throws JDBCClientException {
        try {
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(autoCommit);
            return connection;
        } catch (SQLException e) {
            throw new JDBCClientException(e.getMessage(), e);
        }
    }

    public void execute(Connection connection, String sql) throws JDBCClientException {
        LOGGER.debug("execute sql: {}", sql);
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new JDBCClientException(e.getMessage(), e);
        }
    }

    public int executeUpdate(Connection connection, String sql, Object... params) throws JDBCClientException {
        LOGGER.debug("execute query with result: {}", sql);
        int result;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            setStatementParam(statement, params);
            result = statement.executeUpdate();
            statement.closeOnCompletion();
        } catch (SQLException e) {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e1) {
                }
            }
            throw new JDBCClientException(e.getMessage(), e);
        }

        return result;
    }

    public ResultSet executeQuery(Connection connection, String sql, Object... params) throws JDBCClientException {
        LOGGER.debug("execute query with result: {}", sql);
        ResultSet rs;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            setStatementParam(statement, params);
            rs = statement.executeQuery();
            statement.closeOnCompletion();
        } catch (SQLException e) {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e1) {
                }
            }
            throw new JDBCClientException(sql, e);
        }

        return rs;
    }

    private void setStatementParam(PreparedStatement statement,
        Object[] params) throws SQLException, JDBCClientException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof String) {
                    statement.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    statement.setInt(i + 1, (int) param);
                } else if (param instanceof Double) {
                    statement.setDouble(i + 1, (double) param);
                } else if (param instanceof Long) {
                    statement.setLong(i + 1, (long) param);
                } else {
                    throw new JDBCClientException("Unsupported data type, type=" + param.getClass().getName());
                }
            }
        }
    }

}
