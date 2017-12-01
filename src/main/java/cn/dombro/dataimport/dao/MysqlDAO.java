package cn.dombro.dataimport.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface MysqlDAO {

    void createTable(String tableName, List<String> fields);

    void csvImport(String csvFilePath,String tableName) throws SQLException;

    void csvExport(String tableName,String csvFilePath);

    void dropTable(String tableName);

    void sqlExport(String tableName, String sqlFile,int mode) throws IOException;

    //void sqlExportOnLinux(String tableNam ,String sockFile,String sqlFile);

    boolean isTableExist(String tableName);

    List<String> getFields(String tableName);

    void sqlImport(String sqlFilePath) throws IOException;

    void csvImportByLf(String csvFilePath,String tableName) throws SQLException;
}
