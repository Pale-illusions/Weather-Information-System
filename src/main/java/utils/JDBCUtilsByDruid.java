package utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote 基于Druid数据库连接池的工具类
 */
public class JDBCUtilsByDruid {
    private static DataSource ds;

    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/druid.properties"));
            ds = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    // 开始事务
    public static void beginTransaction(Connection connection) throws SQLException {
        if (connection != null) {
            connection.setAutoCommit(false);
        }
    }

    // 提交事务
    public static void commitTransaction(Connection connection) throws SQLException {
        if (connection != null) {
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    // 回滚事务
    public static void rollbackTransaction(Connection connection) throws SQLException {
        if (connection != null) {
            connection.rollback();
            connection.setAutoCommit(true);
        }
    }

    // 回滚到指定的savepoint
    public static void rollbackToSavepoint(Connection connection, Savepoint savepoint) throws SQLException {
        if (connection != null && savepoint != null) {
            connection.rollback(savepoint);
        }
    }

    // 设置savepoint
    public static Savepoint createSavepoint(Connection connection) throws SQLException {
        if (connection != null) {
            return connection.setSavepoint();
        }
        return null;
    }

    // 关闭连接
    public static void close(ResultSet resultSet, Statement statement, Connection connection) {
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
