package cn.dombro.dataimport.controller;


import cn.dombro.dataimport.factory.IServiceFactory;
import cn.dombro.dataimport.factory.ServiceFactory;
import cn.dombro.dataimport.interceptor.AccressControlInterceptor;
import cn.dombro.dataimport.service.*;
import cn.dombro.dataimport.util.FilePathEnum;
import cn.dombro.dataimport.util.WebTokenUtil;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;

import java.io.File;
import java.util.*;


@Before(AccressControlInterceptor.class)
public class SystemFunctionController extends Controller {

    private Map<String,Object> jsonMap = null;

    private Map<String,Object> claimMap = null;

    //依赖工厂接口
    private IServiceFactory serviceFactory = null;

    private IImportDbService importDbService = null;

    private IExportDbService exportDbService = null;

    private IChangeDbService changeDbService = null;

    private IChangeFileService changeFileService = null;

    private String code = null;

    private String msg = null;

    private String exportFilePath = null;

    private String downLoadToken = null;


    @ActionKey("/sf/dataimoprt/getheader")
    public void getheader(){
        //接收文件
        UploadFile uploadFile = getFile("file");
        //对文件进行改名
        String reNamePath = getReNamePath(uploadFile);
        System.out.println(reNamePath);
        //得到别名
        String fileMark = reNamePath.substring(reNamePath.lastIndexOf(File.separator)+1);
        System.out.println(fileMark);
        jsonMap = new HashMap<>();
        serviceFactory = ServiceFactory.getServiceFactory();
        importDbService = serviceFactory.getImportDbService();
        List<String> headerList = importDbService.getHeaders(reNamePath);
        //操作码
        code = "H001";
        msg = importDbService.getMsg();
        System.out.println(msg);
        if (headerList != null){
            code = "H000";
        }
        jsonMap.put("filemark",fileMark);
        jsonMap.put("headers",headerList);
        jsonMap.put("msg",msg);
        jsonMap.put("code",code);
        renderJson(jsonMap);
    }



    @ActionKey("/sf/dataimoprt/doimport")
    public void doimport(){
        jsonMap = new HashMap<>();
        serviceFactory = ServiceFactory.getServiceFactory();
        importDbService = serviceFactory.getImportDbService();
        code = "I001";
        //获取要导入的品牌
        int brand = getParaToInt("brand");
        //获取表名
        String tableName = getPara("table");
        //获取列名
        String[] fields = getParaValues("fields");
        List fieldList = Arrays.asList(fields);
        //获取更改后文件名
        String fileMark = getPara("filemark");
        switch (brand){
            case 0 :
                if (importDbService.importMysql(tableName,fieldList,fileMark))
                    code = "I000";
                 break;
            case 1 :
                if (importDbService.importMongodb(tableName,fieldList,fileMark))
                    code = "I000";
                break;
        }
        msg = importDbService.getMsg();
        jsonMap.put("code",code);
        jsonMap.put("msg",msg);
        if (code.equals("I000")){
            exportFilePath = importDbService.getExportFilePath();
            downLoadToken = getDownLoadToken(exportFilePath);
            jsonMap.put("token",downLoadToken);
        }
        renderJson(jsonMap);
    }


    public void exportformdb(){
        jsonMap = new HashMap<>();
        serviceFactory = ServiceFactory.getServiceFactory();
        exportDbService = serviceFactory.getExportDbService();
        UploadFile uploadFile = getFile("file");
        String reNamePath = getReNamePath(uploadFile);
        int brand = getParaToInt("brand");
        String tableName = getPara("table");
        String format = getPara("format");
        code = "E001";
        switch (brand){
            case 0 :
                if (exportDbService.exportFromMysql(reNamePath,tableName,format))
                    code = "E000";
                break;

            case 1:
                List fieldList = Arrays.asList(getParaValues("fields"));
                if (exportDbService.exportFromMongodb(reNamePath,tableName,fieldList,format))
                    code = "E000";
                break;
        }
        msg = exportDbService.getMsg();
        jsonMap.put("code",code);
        jsonMap.put("msg",msg);
        if (code.equals("E000")){
            //导出路径
            exportFilePath = exportDbService.getExportFilePath();
            downLoadToken = getDownLoadToken(exportFilePath);
            jsonMap.put("token",downLoadToken);
        }
        renderJson(jsonMap);
    }


    public void changebrand(){
        serviceFactory = ServiceFactory.getServiceFactory();
        changeDbService = serviceFactory.getChangeDbService();
        jsonMap = new HashMap<>();
        code = "C001";
        UploadFile uploadFile = getFile("file");
        String reNamePath = getReNamePath(uploadFile);
        String tableName = getPara("table");
        int operation = getParaToInt("operation");
        switch (operation){
            case 0:
                if(changeDbService.mysqlToMongodb(reNamePath,tableName))
                    code = "C000";
                break;
            case 1:
                List fieldList = Arrays.asList(getParaValues("fields"));
                if (changeDbService.mongodbToMysql(reNamePath,tableName,fieldList))
                    code = "C000";
                break;
        }
        msg = changeDbService.getMsg();
        jsonMap.put("code",code);
        jsonMap.put("msg",msg);
        if (code.equals("C000")){
            exportFilePath = changeDbService.getExportFilePath();
            downLoadToken = getDownLoadToken(exportFilePath);
            jsonMap.put("token",downLoadToken);
        }
        renderJson(jsonMap);

    }

    public void changefileformat(){
        serviceFactory = ServiceFactory.getServiceFactory();
        changeFileService = serviceFactory.getChangeFileService();
        jsonMap = new HashMap<>();
        code = "F001";
        UploadFile uploadFile = getFile("file");
        String reNamePath = getReNamePath(uploadFile);
        int operation = getParaToInt("operation");
        switch (operation){
            case 0:
                if (changeFileService.csvToXls(reNamePath))
                    code = "F000";
                break;
            case 1:
                if (changeFileService.csvToXlsx(reNamePath))
                    code = "F000";
                break;
            case 2:
                if (changeFileService.xlsToCsv(reNamePath))
                    code = "F000";
                break;
            case 3:
                if (changeFileService.xlsToXlsx(reNamePath))
                    code = "F000";
                break;
            case 4:
                if (changeFileService.xlsxToCsv(reNamePath))
                    code = "F000";
                break;
            case 5:
                if (changeFileService.xlsxToXls(reNamePath))
                    code = "F000";
                break;
        }
        msg = changeFileService.getMsg();
        jsonMap.put("code",code);
        jsonMap.put("msg",msg);
        if (code.equals("F000")){
            exportFilePath = changeFileService.getExportFilePath();
            downLoadToken = getDownLoadToken(exportFilePath);
            jsonMap.put("token",downLoadToken);
        }
        renderJson(jsonMap);


    }

    private String getReNamePath(UploadFile uploadFile){
        String reNamePath = FilePathEnum.RENAME_FILE.renameFile(uploadFile.getFile().getAbsolutePath());
        return reNamePath;
    }

    private String getDownLoadToken(String exportFilePath){
        claimMap = new HashMap<>();
        claimMap.put("exportFilePath",exportFilePath);
        downLoadToken = WebTokenUtil.createJavaWebToken(claimMap);
        return downLoadToken;
    }



}
