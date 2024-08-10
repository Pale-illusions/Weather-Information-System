package DAO;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.JDBCUtilsByDruid;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote BasicDAO是其他 DAO 的父类
 */
public class BasicDAO<T> {
    public QueryRunner queryRunner = new QueryRunner();

    /**
     * DML
     * @param sql sql 语句，可以包含 ？
     * @param parameters 传入 ？ 的具体值，可以是多个
     * @return 返回受影响的行数
     */
    public int update(String sql, Object... parameters) {
        return update(null, sql, parameters);
    }

    /**
     * DML + 事务管理
     * @param connection 事务管理的连接
     * @param sql sql 语句，可以包含 ？
     * @param parameters 传入 ？ 的具体值，可以是多个
     * @return 返回受影响的行数
     */
    public int update(Connection connection, String sql, Object... parameters) {
        Connection conn = connection;
        boolean externalConnection = (conn != null);
        try {
            if (conn == null) {
                conn = JDBCUtilsByDruid.getConnection();
                conn.setAutoCommit(false); // 开始事务
            }
            int update = queryRunner.update(conn, sql, parameters);
            if (!externalConnection) {
                conn.commit(); // 提交事务
            }
            return update;
        } catch (SQLException e) {
            if (conn != null && !externalConnection) {
                try {
                    conn.rollback(); // 事务回滚
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw new RuntimeException(e);
        } finally {
            if (conn != null && !externalConnection) {
                JDBCUtilsByDruid.close(null, null, conn);
            }
        }
    }

    /**
     * 多行查询
     * @param sql sql 语句，可以包含 ？
     * @param clazz 传入一个类的Class对象
     * @param parameters 传入 ？ 的具体值，可以是多个
     * @return 根据 Class 返回对应的 ArrayList 集合
     */
    public List<T> multipleQuery(String sql, Class<T> clazz, Object... parameters) {
        return multipleQuery(null, sql, clazz, parameters);
    }

    /**
     * 多行查询（配合事务管理）
     * @param sql sql 语句，可以包含 ？
     * @param clazz 传入一个类的Class对象
     * @param parameters 传入 ？ 的具体值，可以是多个
     * @return 根据 Class 返回对应的 ArrayList 集合
     */
    public List<T> multipleQuery(Connection connection, String sql, Class<T> clazz, Object... parameters) {
        boolean isNull = connection == null;
        try {
            if (isNull) connection = JDBCUtilsByDruid.getConnection();
            return queryRunner.query(connection, sql, new BeanListHandler<T>(clazz), parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (isNull) {
                JDBCUtilsByDruid.close(null, null, connection);
            }
        }
    }

    /**
     * 多行查询（配合事务管理）支持分页功能 + 排序功能
     * @param connection 可选的事务管理连接
     * @param sql sql 语句，可以包含 ？ 但不需要添加 LIMIT 和 OFFSET
     * @param clazz 传入一个类的Class对象
     * @param value 排序对象
     * @param order 排序方式
     * @param pageNo 分页起始页
     * @param pageSize 每页记录数量
     * @param parameters 传入 ？ 的具体值，可以是多个
     * @return 根据 Class 返回对应的 ArrayList 集合
     */
    public List<T> orderedMultipleQueryWithPagination(Connection connection, String sql, Class<T> clazz, String value, String order, int pageNo, int pageSize, Object... parameters) {
        boolean isNull = connection == null;
        try {
            if (isNull) connection = JDBCUtilsByDruid.getConnection();

            // 计算分页偏移量
            int offset = (pageNo - 1) * pageSize;

            // 修改 SQL 语句，添加 ORDER BY, LIMIT 和 OFFSET 子句
            if (value.isEmpty() || order.isEmpty()) {
                sql += " LIMIT ? OFFSET ?";
            } else {
                sql += " ORDER BY " + value + " " + order + " LIMIT ? OFFSET ?";
            }

            // 构建新的参数数组，包含原有参数和分页参数
            Object[] newParams = new Object[parameters.length + 2];
            System.arraycopy(parameters, 0, newParams, 0, parameters.length);
            newParams[parameters.length] = pageSize;  // 设置 LIMIT
            newParams[parameters.length + 1] = offset;  // 设置 OFFSET

            // 执行查询
            return queryRunner.query(connection, sql, new BeanListHandler<T>(clazz), newParams);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (isNull) {
                JDBCUtilsByDruid.close(null, null, connection);
            }
        }
    }

    /**
     * 单行查询
     * @param sql sql 语句，可以包含 ？
     * @param clazz 传入一个类的Class对象
     * @param parameters 传入 ？ 的具体值，可以是多个
     * @return 根据 Class 返回对应的 Class 对象
     */
    public T singleQuery(String sql, Class<T> clazz, Object... parameters) {
        return singleQuery(null, sql, clazz, parameters);
    }

    /**
     * 单行查询（配合事务管理）
     * @param sql sql 语句，可以包含 ？
     * @param clazz 传入一个类的Class对象
     * @param parameters 传入 ？ 的具体值，可以是多个
     * @return 根据 Class 返回对应的 Class 对象
     */
    public T singleQuery(Connection connection, String sql, Class<T> clazz, Object... parameters) {
        boolean isNull = connection == null;
        try {
            if (isNull) connection = JDBCUtilsByDruid.getConnection();
            return queryRunner.query(connection, sql, new BeanHandler<T>(clazz), parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (isNull) {
                JDBCUtilsByDruid.close(null, null, connection);
            }
        }
    }

    /**
     * 查询单行单列，即返回单个值的查询
     * @param sql sql 语句，可以包含 ？
     * @param parameters 传入 ？ 的具体值，可以是多个
     * @return Object 对象
     */
    public Object scalarQuery(String sql, Object... parameters) {
        return scalarQuery(null, sql, parameters);
    }

    /**
     * 查询单行单列，即返回单个值的查询（配合事务管理）
     * @param sql sql 语句，可以包含 ？
     * @param parameters 传入 ？ 的具体值，可以是多个
     * @return Object 对象
     */
    public Object scalarQuery(Connection connection, String sql, Object... parameters) {
        boolean isNull = connection == null;
        try {
            if (isNull) connection = JDBCUtilsByDruid.getConnection();
            return queryRunner.query(connection, sql, new ScalarHandler(), parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (isNull) {
                JDBCUtilsByDruid.close(null, null, connection);
            }
        }
    }
}
