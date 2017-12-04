package cn.dombro.dataimport.dao;

import cn.dombro.dataimport.util.CmdUtil;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MysqlDAOImp implements MysqlDAO{

    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlDAOImp.class);

    private static final String database = PropKit.use("config.properties").get("mysqldatabase");

    private static final String password = PropKit.use("config.properties").get("password");

    private static final String sockFile = PropKit.use("config.properties").get("sockfile");

    private static final String user = PropKit.use("config.properties").get("user");

    private static final String osName = System.getProperty("os.name");

    //MySql动态建表
    public void createTable(String tableName, List<String> fields){
        String sql = "CREATE TABLE "+tableName
                +"( ";
        if (fields != null && fields.size() > 0){
            for (int i = 0;i < fields.size();i++){
                sql += fields.get(i).trim() + " varchar(50)";
                //拼接逗号
                if (i < fields.size() - 1){
                    sql += ",";
                }
            }
        }
        //设置默认字符集
        sql += ");";
        System.out.println(sql);
        Db.update(sql);
        LOGGER.info("在 MySql 数据库中新建 "+tableName+" 表");
    }

    //导入csv文件
    public void csvImport(String csvFilePath,String tableName) throws SQLException {
        if (osName.startsWith("Windows")){
            csvFilePath = csvFilePath.replaceAll("\\\\","\\\\\\\\");
        }
        String line = System.lineSeparator();
        System.out.println(line);
        String sql = "load data local infile '"+csvFilePath+"' into table "+tableName+" character set utf8 fields terminated by ',' " +
                "optionally enclosed by '\"' lines terminated by '"+line+"'";
        System.out.println(sql);
        Db.update(sql);
        LOGGER.info("向"+tableName+"表中导入"+csvFilePath);
    }

    @Override
    public void csvImportByLf(String csvFilePath, String tableName) throws SQLException {
        if (osName.startsWith("Windows")){
         csvFilePath = csvFilePath.replaceAll("\\\\","\\\\\\\\");
        }
        String sql = "load data local infile '"+csvFilePath+"' into table "+tableName+" character set utf8 fields terminated by ',' " +
                "optionally enclosed by '\"' lines terminated by '\\n'";
        Db.update(sql);
        LOGGER.info("向"+tableName+"表中导入"+csvFilePath);
    }



    //导出csv文件
    public void csvExport(String tableName,String csvFilePath){
        //Windows 环境下替换 \ 为 \\
        if (osName.startsWith("Window")){
         csvFilePath = csvFilePath.replaceAll("\\\\","\\\\\\\\");
        }
        System.out.println(csvFilePath);
        String line = System.getProperty("line.separator");
        String sql = "SELECT * FROM "+tableName+" into outfile '"+ csvFilePath+
                "' fields terminated by ',' optionally enclosed by '\"' escaped by '\"' " +
                "lines terminated by '"+line+"'";
        System.out.println(sql);
        Db.queryBoolean(sql);
        LOGGER.info("从"+tableName+"表中导出"+csvFilePath);
    }


    //删除表
    public void dropTable(String tableName){
        String sql = "drop table "+tableName;
        Db.update(sql);
        LOGGER.info("删除表 "+tableName);
    }

    //导出源文件通用
    public void sqlExport(String tableName, String sqlFile,int mode) throws IOException {
        String exportCmd = "";
        String exportShell = "";
        switch (mode){
            case 0 :
                exportCmd = "mysqldump -u "+user+" -p"+password+" "+database+" "+tableName + " -c > "+sqlFile;
                exportShell =  "mysqldump -u "+user+" -S "+sockFile+" "+database+" "+tableName + " -c > "+sqlFile;
                break;
            case 1 :
                exportCmd = "mysqldump -u "+user+" -p"+password+" -t "+database+" "+tableName + " -c > "+sqlFile;
                exportShell =  "mysqldump -u "+user+" -S "+sockFile+" -t "+database+" "+tableName + " -c > "+sqlFile;
                break;
        }
        if (osName.startsWith("Windows")){
            CmdUtil.windowsCmd(exportCmd);
        }else {
            CmdUtil.linuxCmd(exportShell);
        }
        LOGGER.info("从"+tableName+"表中导出 .sql 文件");
    }

    //导出源文件Linux
//    public void sqlExportOnLinux(String tableName,String sockFile,String sqlFile){
//        String exportShell =  "mysqldump -u root -S "+sockFile+" "+database+" "+tableName + " > "+sqlFile;
//        CmdUtil.linuxCmd(exportShell);
//        LOGGER.info("从"+tableName+"表中导出 .sql 文件");
//    }

    //判断表是否存在
    public boolean isTableExist(String tableName){

        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='"+database+"' AND TABLE_NAME='"+tableName+"'";

        String table = Db.queryFirst(sql);
        if (table != null){
            LOGGER.info(database+"库中存在表 "+tableName);
            return true;
        }
        LOGGER.info(database+"库中不存在表 "+tableName);
        return false;
    }

    public List<String> getFields(String tableName){
        String sql = "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '"+database+"' AND TABLE_NAME = '"+tableName+"'";
        LOGGER.info("获取 "+database+"库下 "+tableName+"表中所有字段");
        return Db.query(sql);
    }


    //导入源文件
    public void sqlImport(String sqlFilePath) throws IOException {
        String sourceCmd = "mysql -u "+user+" -p"+password+" -D "+database+" < "+sqlFilePath;
        if (osName.startsWith("Windows")){
            CmdUtil.windowsCmd(sourceCmd);
        }else {
            CmdUtil.linuxCmd(sourceCmd);
        }
        LOGGER.info("导入源文件"+sqlFilePath);
    }




}
