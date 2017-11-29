package cn.dombro.dataimport.service;


import cn.dombro.dataimport.dao.MongodbDAO;
import cn.dombro.dataimport.dao.MongodbDAOImp;
import cn.dombro.dataimport.dao.MysqlDAO;
import cn.dombro.dataimport.factory.DAOFactory;
import cn.dombro.dataimport.factory.DAOImpFactory;
import cn.dombro.dataimport.util.CsvUtil;
import cn.dombro.dataimport.util.FilePathEnum;
import cn.dombro.dataimport.util.GeneratorUtil;
import cn.dombro.dataimport.util.MsgEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class ExportDbService implements IExportDbService{

    private final static Logger LOGGER = LoggerFactory.getLogger(ExportDbService.class);

    private MysqlDAO mysqlDAO = null;

    private DAOFactory daoFactory = null;

    private MongodbDAO mongodbDAO = null;

    private String prefixName = null;

    private String csvFilePath = null;

    private String exportFilePath = null;

    private String msg = null;

    public String getMsg() {
        return msg;
    }

    public String getExportFilePath() {
        return exportFilePath;
    }

    //从 mysql 中导出
    public boolean exportFromMysql(String uploadFilePath, String tableName, String exportFormat) {
        daoFactory = DAOImpFactory.getDaoImpFactory();
        mysqlDAO = daoFactory.getMySqlDAO();
        String prefix = uploadFilePath.substring(uploadFilePath.lastIndexOf(File.separator)+1,uploadFilePath.lastIndexOf("."));
        //判断表是否存在
        if (!mysqlDAO.isTableExist(tableName) && GeneratorUtil.isEngNum(tableName) &&GeneratorUtil.isEngNum(prefix)){

            //设置 .csv 文件放置路径
            setCsvFilePath(uploadFilePath,exportFormat);
            try {
                if (GeneratorUtil.readSqlFile(uploadFilePath,tableName)) {
                    //导入sql文件
                    mysqlDAO.sqlImport(uploadFilePath);
                    //导出 .csv
                    mysqlDAO.csvExport(tableName, csvFilePath);
                    //转换 excel
                    csv2excel(exportFormat);
                    //删除表
                    mysqlDAO.dropTable(tableName);
                    msg = MsgEnum.EXPORT_MYSQL_SUCCESS.getMsg();
                    return true;
                }else {
                    msg = MsgEnum.EXPORT_MYSQL_WITHWRONGTABLE.getMsg();
                    return false;
                }
            } catch (Exception e) {
                System.out.println(csvFilePath);
                //如果sql文件导入后创建该表，则删除表
                if (mysqlDAO.isTableExist(tableName)){
                    mysqlDAO.dropTable(tableName);
                }
                LOGGER.error("MySql导出数据库失败",e);
                msg = MsgEnum.EXPORT_MYSQL_FAIL.getMsg();
                return false;
            }finally {
                //删除sql、csv
                System.out.println(deleteFile(uploadFilePath));
            }
        }else {
            msg = MsgEnum.EXPORT_MYSQL_FAIL.getMsg();
            FilePathEnum.DELETE_FILE.deleteFile(uploadFilePath);
            return false;
        }
    }

    public boolean exportFromMongodb(String uploadFilePath,String collectionName,List<String> fields,String exportFormat){
         daoFactory = DAOImpFactory.getDaoImpFactory();
         mongodbDAO = daoFactory.getMongodbDAO();
         mongodbDAO = new MongodbDAOImp();
         //判断集合是否存在
         if (!mongodbDAO.isCollectionExist(collectionName) && GeneratorUtil.isEngNum(collectionName)){
             //建集合
             mongodbDAO.createCollection(collectionName);
             setCsvFilePath(uploadFilePath,exportFormat);
             try {
                 //导入.json 文件
                 mongodbDAO.jsonImport(collectionName,uploadFilePath);
                 //导出.csv
                 mongodbDAO.csvExport(fields,collectionName,csvFilePath);
                 //转换 excel
                 csv2excel(exportFormat);
                 //删除集合
                 mongodbDAO.dropCollection(collectionName);
                 msg = MsgEnum.EXPORT_MONGODB_SUCCESS.getMsg();
                 return true;
             } catch (Exception e) {
                 System.out.println(csvFilePath);
                 //删除该集合
                 mongodbDAO.dropCollection(collectionName);
                 LOGGER.error("MongoDB导出数据库失败",e);
                 msg = MsgEnum.EXPORT_MONGODB_FAIL.getMsg();
                 return false;
             }finally {
                 //删除 .json、csv
                 deleteFile(uploadFilePath);
             }
         }else{
             msg = MsgEnum.COLLECTION_NAME_ILLEGAL.getMsg();
             FilePathEnum.DELETE_FILE.deleteFile(uploadFilePath);
             return false;
         }
    }

    //判断后缀名，如果为 excel 格式设置csv路径为temp/ 如果为 csv 设置路径为 download/
    private void setCsvFilePath(String uploadFilePath,String exportFormat){
        prefixName = uploadFilePath.substring(uploadFilePath.lastIndexOf(File.separator)+1,uploadFilePath.lastIndexOf("."));
        System.out.println(prefixName);
        if (exportFormat.equals(".csv")){
            csvFilePath = FilePathEnum.DOWNLOAD_PATH.getFilePath()+prefixName+".csv";
            //将 csv 文件路径赋给给导出文件路径
            exportFilePath = csvFilePath;
        }else{
            csvFilePath = FilePathEnum.TEMP_PATH.getFilePath()+prefixName+".csv";
        }
    }

    private void csv2excel(String exportFormat) throws IOException {
        if (exportFilePath == null){
            exportFilePath = FilePathEnum.DOWNLOAD_PATH.getFilePath()+prefixName+exportFormat;
        }
        switch (exportFormat){
            case ".xls":
                CsvUtil.csv2xls(csvFilePath,exportFilePath,0);
                break;
            case ".xlsx":
                CsvUtil.csv2xlsx(csvFilePath,exportFilePath,0);
                break;
            case ".csv":
                break;
        }
    }



    private boolean deleteFile(String uploadFilePath) {
        //如果导出的不是 csv 则删除 csv删除 原文件
        if (! exportFilePath.equals(csvFilePath)){
            if(FilePathEnum.DELETE_FILE.deleteFile(csvFilePath) && FilePathEnum.DELETE_FILE.deleteFile(uploadFilePath))
                return true;
            return false;
        }else {
            return FilePathEnum.DELETE_FILE.deleteFile(uploadFilePath);
        }

    }
}
