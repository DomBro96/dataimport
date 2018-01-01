package cn.dombro.dataimport.service;

import java.util.List;

public interface IImportDbService {

    String getExportFilePath();
    String getMsg();
    List getWarnList();
    List<String> getHeaders(String uploadFilePath);
    boolean importMysql(String tableName, List<String> fields, String fileName,int sqlMode);
    boolean importMongodb(String collectionName, List<String> fields, String fileName);
}
