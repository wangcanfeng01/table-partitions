package com.wcf.util.tp.sql;

public interface PartitionSQL {

    String partitionSQL(String tableName, String colName, Integer partitions);

    String insertFunction(String tableName, String colName, Integer partitions);

    String protectNull(String tableName, String colName);

    String selectAppend(String colName, Integer partitions);

}
