package com.wcf.util.tp.sql;

import com.wcf.util.tp.common.utils.ColNameTranslate;

public class PgHashPartitionSQL implements PartitionSQL {

    /**
     * 功能描述: 生成分区语句，分区语句不支持动态分区，只能在设计阶段预估数据量之后选择好分区个数，然后进行分区
     *
     * @param tableName  主表名称
     * @param colName    分区依赖的字段名
     * @param partitions 分区个数
     * @return 返回信息：分区的执行语句
     * @author wangcanfeng
     * @date 2019/12/3-14:17
     * @since 1.1.100
     */
    @Override
    public String partitionSQL(String tableName, String colName, Integer partitions) {
        StringBuilder ps = new StringBuilder();
        ps.append("do language plpgsql \n");
        ps.append("$$\n");
        ps.append("declare\n");
        ps.append("  parts int := ").append(partitions).append(";\n");
        ps.append("begin\n");
        ps.append("  for i in 0..parts-1 loop \n");
        ps.append("    execute format('create table ").append(tableName).append("%s (like ").append(tableName)
                .append(" including all) inherits (").append(tableName).append(")', i);\n");
        ps.append("    execute format('alter table ").append(tableName)
                .append("%s add constraint ck check(abs(mod(hashtext(")
                .append(colName).append("),%s))=%s)', i, parts, i);\n");
        ps.append("  end loop;\n");
        ps.append("end; \n");
        ps.append("$$;");
        return ps.toString();
    }

    /**
     * 功能描述: 触发器函数，引导数据，将写入到hash值一致的表中
     * 注意： 表名称，列名称，分区数都要和分表语句中的相一致
     *
     * @param tableName  表名称
     * @param colName    分区依赖的字段名
     * @param partitions 分区个数
     * @return 返回信息：分区的执行语句
     * @author wangcanfeng
     * @date 2019/12/3-14:23
     * @since 1.1.100
     */
    @Override
    public String insertFunction(String tableName, String colName, Integer partitions) {
        StringBuilder helper = new StringBuilder();
        helper.append("create or replace function ins_").append(tableName).append("() returns trigger as \n");
        helper.append("$$\n");
        helper.append("declare begin\n");
        helper.append("  case abs(mod(hashtext(NEW.").append(colName).append("),").append(partitions).append("))\n");
        for (int i = 0; i < partitions; i++) {
            helper.append("    when ").append(i).append(" then\n");
            helper.append("      insert into ").append(tableName).append(i).append(" values (NEW.*);\n");
        }
        helper.append("    else\n");
        helper.append("      return NEW;\n");
        helper.append("    end case;\n");
        helper.append("    return null;\n");
        helper.append("end;\n");
        helper.append("$$\n");
        helper.append("language plpgsql strict;\n");
        return helper.toString();
    }

    /**
     * 功能描述: 预防分表依赖的字段为空，为空的情况插入到主表中
     *
     * @param tableName 表名称
     * @param colName   分区依赖的字段名
     * @return 返回信息：
     * @author wangcanfeng
     * @date 2019/12/3-14:28
     * @since 1.1.100
     */
    @Override
    public String protectNull(String tableName, String colName) {
        String pn = "create trigger %s_ins_tg before insert on %s for each row when (NEW.%s is not null) execute procedure ins_%s();";
        return String.format(pn, tableName, tableName, colName, tableName);
    }

    /**
     * 功能描述: 查询的时候需要添加上这个约束条件，在where条件里面用and连接
     * 注意：不加上这个约束将无法加速查询，查询将经历这些表
     *
     * @param colName    分区依赖的字段名，如果插入的参数不是分区依赖的字段，可能会出现异常或无效
     * @param partitions 分区数目
     * @return 返回信息：加速查询的拼接语句
     * @author wangcanfeng
     * @date 2019/12/3-14:35
     * @since 1.1.100
     */
    @Override
    public String selectAppend(String colName, Integer partitions) {
        // 判断要查询的值在哪个分区
        String judgePartition = "and abs(mod(hashtext(%s::text), %d))=abs(mod(hashtext(#{%s}::text), %d))";
        return String.format(judgePartition, colName, partitions, ColNameTranslate.toCamel(colName), partitions);
    }
}
