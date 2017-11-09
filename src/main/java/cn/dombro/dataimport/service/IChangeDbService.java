package cn.dombro.dataimport.service;

import java.util.List;

public interface IChangeDbService {

    String getMsg();
    String getExportFilePath();
    boolean mysqlToMongodb(String dbFilePath,String tableName);
    boolean mongodbToMysql(String dbFilePath,String tableName,List<String> fields);

}
