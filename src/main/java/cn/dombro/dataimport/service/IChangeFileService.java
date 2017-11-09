package cn.dombro.dataimport.service;

public interface IChangeFileService {

    String getMsg();
    String getExportFilePath();
    boolean csvToXls(String uploadFilePath);
    boolean csvToXlsx(String uploadFilePath);
    boolean xlsToXlsx(String uploadFilePath);
    boolean xlsxToXls(String uploadFilePath);
    boolean xlsToCsv(String uploadFilePath);
    boolean xlsxToCsv(String uploadFilePath);
}
