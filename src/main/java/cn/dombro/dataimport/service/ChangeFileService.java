package cn.dombro.dataimport.service;

import cn.dombro.dataimport.util.CsvUtil;
import cn.dombro.dataimport.util.ExcelUtil;
import cn.dombro.dataimport.util.FilePathEnum;
import cn.dombro.dataimport.util.MsgEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.File;
public class ChangeFileService implements IChangeFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeDbService.class);

    private String exportFilePath = null;

    private String tempFilePath = null;

    private String suffix = null;

    private File file = null;

    private String msg = null;

    public String getMsg() {
        return msg;
    }

    public String getExportFilePath() {
        return exportFilePath;
    }

    public boolean csvToXls(String uploadFilePath)  {
        //获取上传文件的后缀名
        getSuffix(uploadFilePath);
        if (file.exists() && suffix.equals(".csv")){
            //设置文件下载路径
            setExportFilePath(uploadFilePath,".xls");
            try {
                CsvUtil.csv2xls(uploadFilePath,exportFilePath,1);
                msg = MsgEnum.CHANGE_FILE_SUCCESS.getMsg();
                return true;
            } catch (IOException e) {
                 LOGGER.error("csv文件转换xls失败",e);
                 msg = MsgEnum.CHANGE_FILE_FAIL.getMsg();
                 return false;
            }finally {
                deleteFile(uploadFilePath);
            }
        }else {
            deleteFile(uploadFilePath);
            msg = MsgEnum.CHANGE_FILE_FAIL.getMsg();
            return false;
        }

    }

    public boolean csvToXlsx(String uploadFilePath)  {
        getSuffix(uploadFilePath);
        if (file.exists() && suffix.equals(".csv")){
            setExportFilePath(uploadFilePath,".xlsx");
            try {
                CsvUtil.csv2xlsx(uploadFilePath,exportFilePath,1);
                msg = MsgEnum.CHANGE_FILE_SUCCESS.getMsg();
                return true;
            } catch (IOException e) {
                LOGGER.error("csv文件转换xlsx失败",e);
                msg = MsgEnum.CHANGE_FILE_FAIL.getMsg();
                return false;
            }finally {
                deleteFile(uploadFilePath);
            }

        }else {
            deleteFile(uploadFilePath);
            msg = MsgEnum.CHANGE_FILE_FAIL.getMsg();
            return false;
        }

    }

    public boolean xlsToXlsx(String uploadFilePath)  {
        getSuffix(uploadFilePath);
        if (file.exists() && suffix.equals(".xls")){
            setExportFilePath(uploadFilePath,".xlsx");
            try {
                ExcelUtil.xls2Xlsx(uploadFilePath,tempFilePath,exportFilePath);
                msg = MsgEnum.CHANGE_FILE_SUCCESS.getMsg();
                return true;
            } catch (Exception e) {
                LOGGER.error("xls文件转换xlsx失败",e);
                msg = MsgEnum.CHANGE_FILE_FAIL.getMsg();
                return false;
            }finally {
                deleteFile(uploadFilePath);
            }
        }else {
            deleteFile(uploadFilePath);
            msg = MsgEnum.CHANGE_FILE_FAIL.getMsg();
            return false;
        }

    }

    public boolean xlsxToXls(String uploadFilePath)  {
        System.out.println(uploadFilePath);
        getSuffix(uploadFilePath);
        if (file.exists() && suffix.equals(".xlsx")){
            setExportFilePath(uploadFilePath,".xls");
            try {
                ExcelUtil.xlsx2Xls(uploadFilePath,tempFilePath,exportFilePath);
                msg = MsgEnum.CHANGE_FILE_SUCCESS.getMsg();
                return true;
            } catch (Exception e) {
                LOGGER.error("xlsx文件转换xls失败",e);
                msg = MsgEnum.CHANGE_FILE_FAIL.getMsg();
                return false;
            }finally {
                 deleteFile(uploadFilePath);
            }
        }else {
            deleteFile(uploadFilePath);
            msg = MsgEnum.CHANGE_FILE_FAIL.getMsg();
            return false;
        }

    }

    public boolean xlsToCsv(String uploadFilePath)  {
        getSuffix(uploadFilePath);
        if (file.exists() && suffix.equals(".xls")){
            setExportFilePath(uploadFilePath,".csv");
            try {
                ExcelUtil.xls2CsvByGbk(uploadFilePath,exportFilePath);
                msg = MsgEnum.CHANGE_FILE_SUCCESS.getMsg();
                return true;
            } catch (Exception e) {
                LOGGER.error("xls文件转换csv失败",e);
                msg = MsgEnum.CHANGE_FILE_FAIL.getMsg();
                return false;
            }finally {
                deleteFile(uploadFilePath);
            }
        }else {
            deleteFile(uploadFilePath);
            msg = MsgEnum.CHANGE_FILE_FAIL.getMsg();
            return false;
        }

    }

    public boolean xlsxToCsv(String uploadFilePath)  {
        getSuffix(uploadFilePath);
        if (file.exists() && suffix.equals(".xlsx")){
            setExportFilePath(uploadFilePath,".csv");
            try {
                ExcelUtil.xlsx2CsvByGbk(uploadFilePath,exportFilePath);
                msg = MsgEnum.CHANGE_FILE_SUCCESS.getMsg();
                return true;
            } catch (Exception e) {
                LOGGER.error("xlsx文件转换csv失败",e);
                msg = MsgEnum.CHANGE_FILE_FAIL.getMsg();
                return false;
            }finally {
                deleteFile(uploadFilePath);
            }
        }else {
            deleteFile(uploadFilePath);
            msg = MsgEnum.CHANGE_FILE_FAIL.getMsg();
            return false;
        }
    }

    private void setExportFilePath(String uploadFilePath,String formart){
       String prefixName = uploadFilePath.substring(uploadFilePath.lastIndexOf(File.separator)+1,uploadFilePath.lastIndexOf("."));
       String suffix = uploadFilePath.substring(uploadFilePath.lastIndexOf("."));
       //如果是 xlsToXlsx 或 xlsxToXls 为 tempFilePath 赋值
       if (suffix.equals(".xls")&&formart.equals(".xlsx") || suffix.equals(".xlsx")&&formart.equals(".xls")){
           tempFilePath = FilePathEnum.TEMP_PATH.getFilePath()+prefixName+".csv";
       }
       exportFilePath = FilePathEnum.DOWNLOAD_PATH.getFilePath()+prefixName+formart;
    }

    private void getSuffix(String uploadFilePath){
        file = new File(uploadFilePath);
        suffix = uploadFilePath.substring(uploadFilePath.lastIndexOf("."));
    }


    private boolean deleteFile(String uploadFilePath) {
        if (tempFilePath != null && !tempFilePath.equals("")){
            if (FilePathEnum.DELETE_FILE.deleteFile(uploadFilePath) && FilePathEnum.DELETE_FILE.deleteFile(tempFilePath))
                return true;
            return false;
        }else{
            return FilePathEnum.DELETE_FILE.deleteFile(uploadFilePath);
        }
    }

}
