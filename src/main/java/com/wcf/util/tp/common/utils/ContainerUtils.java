package com.wcf.util.tp.common.utils;

import com.alibaba.druid.pool.DruidDataSource;
import javafx.stage.Stage;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangcanfeng
 * @time 2019/5/17
 * @function 容器工具，自制容器只支持取用，不支持设置，除非初始化
 **/
public class ContainerUtils {

    private ContainerUtils() {
        // 工具类的构造函数
    }

    public enum DBType {
        POSTGRES, MYSQL
    }

    /**
     * 用于操作数据库
     */
    private volatile static JdbcTemplate template;

    /**
     * 数据库连接池
     */
    private volatile static DruidDataSource dataSource;

    /**
     * 主窗口
     */
    private volatile static Stage primaryStage;

    /**
     * pg的驱动
     */
    private static String pgDriverName = "org.postgresql.Driver";

    /**
     * pg的驱动
     */
    private static String mysqlDriverName = "com.mysql.cj.jdbc.Driver";

    /**
     * 当前采用的数据库类型
     */
    private static DBType dbType;

    private static int initialSize = 10;

    private static int minIdle = 5;

    private static int maxActive = 20;

    private static int maxWait = 60000;

    /**
     * 要打开的表名
     */
    private static String tableName;

    /**
     * 字段和要插入的值的map
     */
    public static Map<String, Object> sqlStatementMap = new ConcurrentHashMap<>();

    /**
     * 功能描述：  初始化template
     *
     * @param ip     数据库ip
     * @param port   数据库端口
     * @param dbName 数据库名
     * @param user   用户名
     * @param pwd    密码
     * @param type   数据库类型
     * @author wangcanfeng
     * @time 2019/5/17 22:12
     * @since v1.0
     **/
    public static synchronized boolean setTemplate(String ip, String port, String dbName, String user, String pwd, DBType type) {
        if (dataSource != null) {
            dataSource.close();
        }
        dataSource = new DruidDataSource();
        switch (type) {
            case MYSQL: {
                dataSource.setDriverClassName(mysqlDriverName);
                dataSource.setUrl("jdbc:mysql://" + ip + ":" + port + "/" + dbName
                        + "?serverTimezone=Shanghai&useSSL=false&serverTimezone=GMT%2B8&allowMultiQueries=true");
                dbType = DBType.MYSQL;
                break;
            }
            case POSTGRES: {
                dataSource.setDriverClassName(pgDriverName);
                dataSource.setUrl("jdbc:postgresql://" + ip + ":" + port + "/" + dbName
                        + "?useUnicode=true&characterEncoding=utf-8");
                dbType = DBType.POSTGRES;
                break;
            }
            default: {
                dataSource.setDriverClassName(pgDriverName);
                dataSource.setUrl("jdbc:postgresql://" + ip + ":" + port + "/" + dbName
                        + "?useUnicode=true&characterEncoding=utf-8");
                dbType = DBType.POSTGRES;
                break;
            }
        }
        dataSource.setUsername(user);
        dataSource.setPassword(pwd);
        dataSource.setInitialSize(initialSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxWait(maxWait);
        try {
            dataSource.init();
            template = new JdbcTemplate(dataSource);
            return false;
        } catch (SQLException e) {
            dataSource.close();
            return true;
        }
    }

    /**
     * 功能描述：  获取template
     *
     * @param
     * @author wangcanfeng
     * @time 2019/5/17 22:13
     * @since v1.0
     **/
    public static JdbcTemplate getTemplate() {
        return template;
    }

    /**
     * 功能描述: 当前所连接的数据库类型
     *
     * @param
     * @return:com.wcf.test.dbpest.utils.ContainerUtils.DBType
     * @since: v1.1
     * @Author:wangcanfeng
     * @Date: 2019/5/18-10:26
     */
    public static DBType getDbType() {
        return dbType;
    }

    /**
     * 功能描述:注入主窗口到容器中
     *
     * @param stage
     * @return:void
     * @since: v1.1
     * @Author:wangcanfeng
     * @Date: 2019/5/18-9:49
     */
    public static void setPrimaryStage(Stage stage) {
        // 主窗口不允许修改
        if (primaryStage != null) {
            return;
        } else {
            primaryStage = stage;
        }
    }


    /**
     * 功能描述: 从容器中获取到主窗口
     *
     * @param
     * @return:javafx.stage.Stage
     * @since: v1.1
     * @Author:wangcanfeng
     * @Date: 2019/5/18-9:46
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }


    /**
     * 功能描述: 获取要打开的表名
     *
     * @param
     * @return:java.lang.String
     * @since: v1.1
     * @Author:wangcanfeng
     * @Date: 2019/5/20-9:35
     */
    public static String getTableName() {
        return tableName;
    }

    /**
     * 功能描述: 设置要打开的表名
     *
     * @param tbName
     * @return:void
     * @since: v1.1
     * @Author:wangcanfeng
     * @Date: 2019/5/20-9:35
     */
    public static void setTableName(String tbName) {
        tableName = tbName;
    }
}