package cn.dombro.dataimport.util;

import com.jfinal.kit.PropKit;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoDatabase;

public class MongoDbUtil {


    private static MongoClient mongoClient = null;

    static {
        String host = PropKit.use("mongodb.properties").get("host");
        int post = PropKit.use("mongodb.properties").getInt("port");

        mongoClient = new MongoClient(host,post);
        MongoClientOptions.Builder options =  new MongoClientOptions.Builder();
        options.connectionsPerHost(300);// 连接池设置为300个连接,默认为100
        options.connectTimeout(15000);// 连接超时，推荐>3000毫秒
        options.maxWaitTime(5000); //
        options.socketTimeout(0);// 套接字超时时间，0无限制
        options.threadsAllowedToBlockForConnectionMultiplier(5000);// 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
        options.build();
    }

    public static MongoDatabase getDatabase(String dbName){
        if (dbName != null && !"".equals(dbName)){
            MongoDatabase database = mongoClient.getDatabase(dbName);
            return database;
        }

        return null;
    }

    //删除一个集合
    public static void dropCollection(String dbName,String collName){
        getDatabase(dbName).getCollection(collName).drop();
    }

    public static void creatCollection(String dbName,String collName){
        getDatabase(dbName).createCollection(collName);
    }

}
