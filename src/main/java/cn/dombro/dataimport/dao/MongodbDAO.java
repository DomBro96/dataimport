package cn.dombro.dataimport.dao;

//MongoDbDAO 接口
import java.io.IOException;
import java.util.List;

public interface MongodbDAO {

     boolean isCollectionExist(String collectionName);

     void createCollection(String collectionName);

     void dropCollection(String collection);

     void csvImport(List<String> fieldList, String collection, String filePath) throws IOException;

     void csvExport(List<String> fieldList,String collection,String filePath) throws IOException;

     void jsonImport(String collection,String filePath) throws IOException;

     void jsonExport(String collection,String filePath) throws IOException;

    }
