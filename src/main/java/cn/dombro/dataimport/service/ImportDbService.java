package cn.dombro.dataimport.service;

import cn.dombro.dataimport.dao.MongodbDAO;
import cn.dombro.dataimport.dao.MysqlDAO;
import cn.dombro.dataimport.factory.DAOFactory;
import cn.dombro.dataimport.factory.DAOImpFactory;
import cn.dombro.dataimport.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ImportDbService implements IImportDbService{

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportDbService.class);

    private MysqlDAO mysqlDAO = null;

    private MongodbDAO mongodbDAO = null;

    private DAOFactory daoFactory = null;

    private String excelFilePath = null;

    private String csvFilePath = null;

    private String exportFilePath = null;

    private String suffix = null;

    private String msg = null;

    private List warnList = null;

    public List getWarnList() {

        return warnList;
    }

    public String getExportFilePath() {
        return exportFilePath;
    }

    public String getMsg() {
        return msg;
    }




    /**
     *
     * @param uploadFilePath 上传文件绝对路径
     * @return
     */
    public List<String> getHeaders(String uploadFilePath)   {
        String suffix = uploadFilePath.substring(uploadFilePath.lastIndexOf("."));
        msg = MsgEnum.GET_FILE_HEADER_SUCCESS.getMsg();
        try{
            switch (suffix){
                case ".csv" :
                    return CsvUtil.getHeader(uploadFilePath);
                case ".xls" :
                    return ExcelUtil.getXlsHeader(uploadFilePath);
                case ".xlsx" :
                    return ExcelUtil.getXlsxHeader(uploadFilePath);
            }
        }catch (Exception e){
            msg = MsgEnum.GET_FILE_HEADER_FAIL.getMsg();
            LOGGER.error("导出文件首行失败",e);
            //删除文件
            FilePathEnum.DELETE_FILE.deleteFile(uploadFilePath);
            return null;
        }
        return null;
    }



    /**
     * @param tableName 表名
     * @param fields    列名列表
     * @param fileName  更改后文件名
     */
    public boolean importMysql(String tableName, List<String> fields, String fileName,int sqlMode)  {
        //调用工厂中单例方法
        daoFactory = DAOImpFactory.getDaoImpFactory();
        mysqlDAO = daoFactory.getMySqlDAO();
        String exportPrefix = fileName.substring(0, fileName.lastIndexOf("."));
        exportFilePath = FilePathEnum.DOWNLOAD_PATH.getFilePath() + exportPrefix + ".sql";
        if (!mysqlDAO.isTableExist(tableName) && GeneratorUtil.isEngNum(tableName)) {
            mysqlDAO.createTable(tableName, fields);
            try {
                excel2csv(fileName);
                //导入 .csv
                mysqlDAO.csvImport(csvFilePath,tableName);
                //导入后查看又没有警告
                if (mysqlDAO.getWarning().size() != 0){
                    warnList = mysqlDAO.getWarning();
                    //删除表
                    mysqlDAO.dropTable(tableName);
                    msg = MsgEnum.IMPORT_MYSQL_FAIL.getMsg();
                    return false;
                }
                //导出 .sql
                mysqlDAO.sqlExport(tableName,exportFilePath,sqlMode);
                //删除表
                mysqlDAO.dropTable(tableName);
                msg = MsgEnum.IMPORT_MYSQL_SUCCESS.getMsg();
                return true;
            } catch (Exception e) {
                msg = MsgEnum.IMPORT_MYSQL_FAIL.getMsg();
                //捕获异常，删除表，返回 false
                mysqlDAO.dropTable(tableName);
                LOGGER.error("导入MySql数据库失败",e);
                return false;
            }finally {
                //删除 csv、excel
                deleteFile();
            }
        }else {
            //如果表存在，则删除原文件，并返回 false
            FilePathEnum.DELETE_FILE.deleteFile(FilePathEnum.UPLOAD_PATH.getFilePath()+fileName);
            msg = MsgEnum.TABLE_NAME_ILLEGAL.getMsg();
            return false;
        }
    }

    public boolean importMongodb(String collectionName, List<String> fields, String fileName)  {
        daoFactory = DAOImpFactory.getDaoImpFactory();
        mongodbDAO = daoFactory.getMongodbDAO();
        String exportPrefix = fileName.substring(0, fileName.lastIndexOf("."));
        //mongoDb对 .json 文件默认支持
        exportFilePath = FilePathEnum.DOWNLOAD_PATH.getFilePath() + exportPrefix + ".json";
        if (!mongodbDAO.isCollectionExist(collectionName) && GeneratorUtil.isEngNum(collectionName)) {
            //新建集合
            mongodbDAO.createCollection(collectionName);
            //excel 导出 csv
            try {
                excel2csv(fileName);
                //导入 csv 文件
                mongodbDAO.csvImport(fields, collectionName, csvFilePath);
                //导出.json 文件
                mongodbDAO.jsonExport(collectionName, exportFilePath);
                //删除集合
                mongodbDAO.dropCollection(collectionName);
                msg = MsgEnum.IMPORT_MONGODB_SUCCESS.getMsg();
                return true;
            } catch (Exception e) {
                //对异常进行捕获，并返回 false
                msg = MsgEnum.IMPORT_MONGODB_FAIL.getMsg();
                mongodbDAO.dropCollection(collectionName);
                LOGGER.error("导入MongoDB失败",e);
                return false;
            }finally {
                //删除 csv、excel
                  deleteFile();
            }
        }else {
            FilePathEnum.DELETE_FILE.deleteFile(FilePathEnum.UPLOAD_PATH.getFilePath()+fileName);
            msg = MsgEnum.COLLECTION_NAME_ILLEGAL.getMsg();
            return false;
        }
    }

    //   通过判断后缀名将excel两种格式转为csv文件
    private void excel2csv(String fileName) throws Exception {
        suffix = fileName.substring(fileName.lastIndexOf("."));
        //判断是否为csv文件
        if (suffix.equals(".csv")) {
            csvFilePath = FilePathEnum.UPLOAD_PATH.getFilePath() + fileName;
        } else {
            String prefix = fileName.substring(0, fileName.lastIndexOf("."));
            String csvFile = prefix + ".csv";
            excelFilePath = FilePathEnum.UPLOAD_PATH.getFilePath() + fileName;
            csvFilePath = FilePathEnum.TEMP_PATH.getFilePath() + csvFile;
            switch (suffix) {
                case ".xls":
                    ExcelUtil.xls2Csv(excelFilePath,csvFilePath);
                    break;
                case ".xlsx":
                    ExcelUtil.xlsx2Csv(excelFilePath,csvFilePath);
                    break;
            }
        }
    }


    private boolean deleteFile() {
        if (suffix.equals(".csv")) {
            return FilePathEnum.DELETE_FILE.deleteFile(csvFilePath);
        } else {
            if (FilePathEnum.DELETE_FILE.deleteFile(csvFilePath) && FilePathEnum.DELETE_FILE.deleteFile(excelFilePath))
                return true;
            return false;
        }
    }
}
