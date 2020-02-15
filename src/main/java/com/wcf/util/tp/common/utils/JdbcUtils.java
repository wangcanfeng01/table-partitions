package com.wcf.util.tp.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JdbcUtils {


    public enum SqlType {
        SELECT, EXECUTE
    }

    /**
     * 功能描述: 使用sql语句查询List
     *
     * @param sql     sql语句
     * @param rowType list中的对象类型
     * @return:java.util.List<T>
     * @since: v1.1
     * @Author:wangcanfeng
     * @Date: 2019/5/18-10:50
     */
    public static <T> List<T> queryList(String sql, Class<T> rowType) {
        checkSql(sql, SqlType.SELECT);
        List<Map<String, Object>> list = ContainerUtils.getTemplate().queryForList(sql);
        if (ObjectUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(list.size());
        list.forEach(map -> result.add(mapper(map, rowType)));
        return result;
    }

    /**
     * 功能描述: 使用sql语句查询单个对象
     *
     * @param sql     sql语句
     * @param rowType 返回对象类型
     * @return:T
     * @since: v1.1
     * @Author:wangcanfeng
     * @Date: 2019/5/18-10:51
     */
    public static <T> T querySingle(String sql, Class<T> rowType) {
        List<T> list = queryList(sql, rowType);
        if (ObjectUtils.isEmpty(list)) {
            return null;
        }
        if (list.size() > 1) {
            throw new IndexOutOfBoundsException("expect 1 result, but " + list.size() + " obtained");
        }
        return queryList(sql, rowType).get(0);
    }

    /**
     * 功能描述: 数据库中的查询结果与java对象之间的映射
     *
     * @param obj     查询结果
     * @param rowType java对象类型
     * @return:T
     * @since: v1.1
     * @Author:wangcanfeng
     * @Date: 2019/5/18-13:50
     */
    private static <T> T mapper(Map<String, Object> obj, Class<T> rowType) {
        JSONObject object = new JSONObject();
        obj.forEach((key, value) -> object.put(key, value));
        return object.toJavaObject(rowType);
    }


    /**
     * 支持批量执行SQL、操作，提高SQL执行效率
     *
     * @param sqln sql语句,至少要有一句语句才能执行
     * @return
     */
    public static int[] executeBatch(String... sqln) {
        if (ObjectUtils.isEmpty(sqln)) {
            return new int[]{0};
        }
        String[] sqls = sqln.clone();
        for (int i = 0; i < sqls.length; i++) {
            checkSql(sqls[i], SqlType.EXECUTE);
        }
        int[] result = ContainerUtils.getTemplate().batchUpdate(sqln);
        return result;
    }



    /**
     * 功能描述: 简单的检查语句是否合法
     *
     * @param sql
     * @param sqlType
     * @return:void
     * @since: v1.1
     * @Author:wangcanfeng
     * @Date: 2019/5/18-11:15
     */
    private static void checkSql(String sql, SqlType sqlType) {
        if (ObjectUtils.isEmpty(sql)) {
            throw new NullPointerException("sql is null");
        }
        switch (sqlType) {
            case SELECT: {
                // 如果不是select开头的语句就不能用于查询
                if (!sql.toLowerCase().startsWith("select")) {
                    throw new UnsupportedOperationException("please check your sql statement's type");
                }
                break;
            }
            case EXECUTE: {
                // 执行语句不能用select开头
                if (sql.toLowerCase().startsWith("select")) {
                    throw new UnsupportedOperationException("please check your sql statement's type");
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("please check your sql statement's type");
            }
        }
    }
}
