package cn.dombro.dataimport.service;

import cn.dombro.dataimport.dao.MongodbDAO;
import cn.dombro.dataimport.dao.MongodbDAOImp;
import cn.dombro.dataimport.dao.MysqlDAO;
import cn.dombro.dataimport.dao.MysqlDAOImp;
import cn.dombro.dataimport.factory.DAOFactory;
import cn.dombro.dataimport.factory.DAOImpFactory;
import cn.dombro.dataimport.util.FilePathEnum;
import cn.dombro.dataimport.util.GeneratorUtil;
import cn.dombro.dataimport.util.MsgEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;


public class ChangeDbService implements IChangeDbService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeDbService.class);

    private DAOFactory daoFactory = null;

    private MysqlDAO mysqlDAO = null;

    private MongodbDAO mongodbDAO = null;

    private String csvFilePath = null;

    private String prefixName = null;

    private String jsonFilePath = null;

    private String sqlFilePath = null;

    private String exportFilePath = null;

    private List<String> fields = null;

    private String msg = null;

    public String getMsg() {
        return msg;
    }

    public String getExportFilePath() {
        return exportFilePath;
    }

    public boolean mysqlToMongodb(String dbFilePath,String tableName){
        daoFactory = DAOImpFactory.getDaoImpFactory();
        mysqlDAO = daoFactory.getMySqlDAO();
        mongodbDAO = daoFactory.getMongodbDAO();
        String prefix = dbFilePath.substring(dbFilePath.lastIndexOf(File.separator)+1,dbFilePath.lastIndexOf("."));
        //判断表是否存在
        if (!mysqlDAO.isTableExist(tableName) && !mongodbDAO.isCollectionExist(tableName) && GeneratorUtil.isEngNum(tableName) &&GeneratorUtil.isEngNum(prefix)){

            try {
                if (GeneratorUtil.readSqlFile(dbFilePath,tableName)) {
                    //导入mysql文件
                    mysqlDAO.sqlImport(dbFilePath);
                    fields = mysqlDAO.getFields(tableName);
                    //设置文件路径
                    setFilePath(dbFilePath);
                    exportFilePath = jsonFilePath;
                    //导出 .csv
                    mysqlDAO.csvExport(tableName, csvFilePath);
                    //MongoDb建集合
                    mongodbDAO.createCollection(tableName);
                    //csv导入MongoDb
                    mongodbDAO.csvImport(fields, tableName, csvFilePath);
                    //MongoDb导出 .json
                    mongodbDAO.jsonExport(tableName, jsonFilePath);
                    //删除表 集合
                    mysqlDAO.dropTable(tableName);
                    mongodbDAO.dropCollection(tableName);
                    msg = MsgEnum.MYSQL_TO_MONGODB_SUCCESS.getMsg();
                    return true;
                }else {
                    msg = MsgEnum.EXPORT_MYSQL_WITHWRONGTABLE.getMsg();
                    return false;
                }
            } catch (IOException e) {
                LOGGER.error("MySql转换MongoDB失败",e);
                msg = MsgEnum.MYSQL_TO_MONGODB_FAIL.getMsg();
                //如果sql文件导入后创建该表，则删除表
                if (mysqlDAO.isTableExist(tableName)){
                    mysqlDAO.dropTable(tableName);
                }
                if (mongodbDAO.isCollectionExist(tableName)){
                    mongodbDAO.dropCollection(tableName);
                }
                return false;
            }finally {
                deleteFile(dbFilePath);
            }
        }else {
            msg = MsgEnum.MYSQL_TO_MONGODB_FAIL.getMsg();
            FilePathEnum.DELETE_FILE.deleteFile(dbFilePath);
            return false;
        }

    }



    public boolean mongodbToMysql(String dbFilePath,String tableName,List<String> fields,int sqlMode){
        daoFactory = DAOImpFactory.getDaoImpFactory();
        mysqlDAO = daoFactory.getMySqlDAO();
        mongodbDAO = daoFactory.getMongodbDAO();
        mongodbDAO = new MongodbDAOImp();
        mysqlDAO = new MysqlDAOImp();
        //判断表 集合是否存在
        if (!mongodbDAO.isCollectionExist(tableName) && !mysqlDAO.isTableExist(tableName) && GeneratorUtil.isEngNum(tableName)){
            //创建集合
            mongodbDAO.createCollection(tableName);
            //设置文件路径
            setFilePath(dbFilePath);
            try {
                //导入 json
                mongodbDAO.jsonImport(tableName,dbFilePath);
                exportFilePath = sqlFilePath;
                //导出 csv
                mongodbDAO.csvExport(fields,tableName,csvFilePath);
                //MySql 建表
                mysqlDAO.createTable(tableName,fields);
                //csv 导入 mysql
                mysqlDAO.csvImportByLf(csvFilePath,tableName);
                //mysql导出 .sql 文件
                mysqlDAO.sqlExport(tableName,sqlFilePath,sqlMode);
                mongodbDAO.dropCollection(tableName);
                mysqlDAO.dropTable(tableName);
                msg = MsgEnum.MONGODB_TO_MYSQL_SUCCESS.getMsg();
                return true;
            } catch (Exception e) {
               LOGGER.error("MongoDB数据库导入MySql数据库失败",e);
               msg = MsgEnum.MONGODB_TO_MYSQL_FAIL.getMsg();
               //删除MongoDB 和 MySql 中的表
               mongodbDAO.dropCollection(tableName);
               if (mysqlDAO.isTableExist(tableName)){
                   mysqlDAO.dropTable(tableName);
               }
               return false;
            }finally {
                //删除文件
                deleteFile(dbFilePath);
            }
        }else {
            msg = MsgEnum.COLLECTION_NAME_ILLEGAL.getMsg();
            FilePathEnum.DELETE_FILE.deleteFile(dbFilePath);
            return false;
        }

    }

    //设置 csv 文件路径
    private void setFilePath(String uploadFilePath){
        prefixName = uploadFilePath.substring(uploadFilePath.lastIndexOf(File.separator)+1,uploadFilePath.lastIndexOf("."));
        csvFilePath = FilePathEnum.TEMP_PATH.getFilePath()+prefixName+".csv";
        jsonFilePath = FilePathEnum.DOWNLOAD_PATH.getFilePath()+prefixName+".json";
        sqlFilePath = FilePathEnum.DOWNLOAD_PATH.getFilePath()+prefixName+".sql";
    }


    private void deleteFile(String uploadFilePath) {
        //删除上传文件
        //删除 csv 文件
        if (new File(uploadFilePath).exists())
            FilePathEnum.DELETE_FILE.deleteFile(uploadFilePath);
        if (csvFilePath != null && !csvFilePath.equals(""))
            FilePathEnum.DELETE_FILE.deleteFile(csvFilePath);
    }
}
