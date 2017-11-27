package cn.dombro.dataimport.util;

public enum MsgEnum {

    GET_FILE_HEADER_SUCCESS("获取首行成功"),
    GET_FILE_HEADER_FAIL("获取文件首行失败，请查看您上传文件格式以及文件首行是否为空"),
    IMPORT_MYSQL_SUCCESS("导入MySql数据库成功"),
    IMPORT_MONGODB_SUCCESS("导入MongoDB数据库成功"),
    IMPORT_MYSQL_FAIL("导入MySql数据库出现异常，请检查上传文件格式与内容是否正确"),
    IMPORT_MONGODB_FAIL("导入MongoDB数据库出现异常，请检查上传文件格式与内容是否正确"),
    COLLECTION_NAME_ILLEGAL("该集合名不合法，请重试"),
    TABLE_NAME_ILLEGAL("该表名不合法，请重试"),
    EXPORT_MYSQL_SUCCESS("MySql数据库文件导出指定格式成功"),
    EXPORT_MONGODB_SUCCESS("MongoDB数据库文件导出指定格式成功"),
    EXPORT_MYSQL_FAIL("MySql数据库导出指定格式失败，.sql文件和表名不能为中文，并且请检查您的原文件是否为表结构的sql文件"),
    EXPORT_MYSQL_WITHWRONGTABLE("MySql数据库导出失败，sql文件中并无您输入的表，请确保您的sql文件为utf-8编码"),
    EXPORT_MONGODB_FAIL("MongoDB数据库导出指定格式失败，请检查您的原文件是否为集合导出的json文件"),
    MYSQL_TO_MONGODB_SUCCESS("MySql数据库转换MongoDB成功"),
    MONGODB_TO_MYSQL_SUCCESS("MongoDB数据库转换MySql成功"),
    MYSQL_TO_MONGODB_FAIL("MySql数据库转换MongoDB失败，.sql文件和表名不能为中文，请检查您的原文件是否为表结构的sql文件"),
    MONGODB_TO_MYSQL_FAIL("MongoDB数据库导入MySql数据库失败,请检查您的上传文件是否为集合导出的json文件，以及您要导入MySql的字段是否存在"),
    CHANGE_FILE_SUCCESS("文件转换成功"),
    CHANGE_FILE_FAIL("文件格式转换失败，请您检查上传文件格式与内容");

    private String msg;

    private MsgEnum(String msg){
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
