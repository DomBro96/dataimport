package cn.dombro.dataimport.service;

import java.util.List;

public interface IExportDbService {

    String getMsg();
    String getExportFilePath();
    boolean exportFromMysql(String uploadFilePath, String tableName, String exportFormat);
    boolean exportFromMongodb(String uploadFilePath, String collectionName, List<String> fields, String exportFormat);

}
