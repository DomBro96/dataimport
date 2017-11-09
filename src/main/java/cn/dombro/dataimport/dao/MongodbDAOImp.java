package cn.dombro.dataimport.dao;


import cn.dombro.dataimport.util.CmdUtil;
import cn.dombro.dataimport.util.MongoDbUtil;
import com.jfinal.kit.PropKit;
import com.mongodb.client.MongoIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MongodbDAOImp implements MongodbDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongodbDAOImp.class);

    private static final String database = PropKit.use("mongodb.properties").get("database");

    private static final String osName = System.getProperty("os.name");

    //判断集合是否存在
    public boolean isCollectionExist(String collectionName){
        List<String> collectionList = new ArrayList<>();
        MongoIterable<String> collectionNames = MongoDbUtil.getDatabase(database).listCollectionNames();
        for (String collection:collectionNames){
            collectionList.add(collection);
        }
        if (collectionList.contains(collectionName)){
            LOGGER.info(database+"库中存在集合"+collectionName);
            return true ;
        }
        LOGGER.info(database+"库中不存在集合"+collectionName);
        return false;
    }

    //建集合
    public void createCollection(String collectionName){
        MongoDbUtil.creatCollection(database,collectionName);
        LOGGER.info("在"+database+"库中新建集合 "+collectionName);
    }


    //删除集合
    public void dropCollection(String collection){
        MongoDbUtil.dropCollection(database,collection);
        LOGGER.info("在"+database+"库中删除集合 "+collection);
    }

    //csv 文件导入
    public void csvImport(List<String> fieldList,String collection,String filePath) throws IOException {
        String fields = "";
        for (int i = 0;i<fieldList.size();i++){
             fields += fieldList.get(i);
             if (i < fieldList.size()-1 ){
                 fields += ",";
             }
        }

        String importCmd = "mongoimport --db "+database+" -f "+fields+" --collection "+collection+" --type csv --file "+filePath;
        if (osName.startsWith("Windows")){
            CmdUtil.windowsCmd(importCmd);
        }else {
            CmdUtil.linuxCmd(importCmd);
        }

        LOGGER.info("向"+collection+"表中导入 csv 文件"+filePath);

    }

    //csv 文件导出
    public void csvExport(List<String> fieldList,String collection,String filePath) throws IOException {
        String fields = "";
        for (int i = 0;i<fieldList.size();i++){
            fields += fieldList.get(i);
            if (i < fieldList.size()-1 ){
                fields += ",";
            }
        }

        String exportCmd = "mongoexport --type=csv -f "+fields+" -d "+database+" -c "+collection+" --noHeaderLine  -o "+filePath;
        if (osName.startsWith("Windows")){
            CmdUtil.windowsCmd(exportCmd);
        }else {
            CmdUtil.linuxCmd(exportCmd);
        }
        LOGGER.info("从"+collection+"表中导出 csv 文件"+filePath);
    }

    // json 文件导入
    public void jsonImport(String collection,String filePath) throws IOException {
       String importCmd =  "mongoimport --db "+database+"  --collection "+collection+" --file "+filePath;
       if (osName.startsWith("Windows")){
            CmdUtil.windowsCmd(importCmd);
        }else {
            CmdUtil.linuxCmd(importCmd);
        }
        LOGGER.info("向"+collection+"表中导入 json 文件"+filePath);
    }

    public void jsonExport(String collection,String filePath) throws IOException {
        String exportCmd = "mongoexport -d "+database+" -c "+collection+" -o "+filePath;
        if (osName.startsWith("Windows")){
            CmdUtil.windowsCmd(exportCmd);
        }else {
            CmdUtil.linuxCmd(exportCmd);
        }
        LOGGER.info("从"+collection+"表中导出 json 文件"+filePath);
    }

}

