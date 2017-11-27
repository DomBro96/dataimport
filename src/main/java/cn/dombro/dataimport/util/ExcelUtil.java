package cn.dombro.dataimport.util;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {



    private static List<String> cellOnRowFist = null;

    public static List<String> getXlsHeader(String filePath) throws IOException {

        InputStream inputStream = new FileInputStream(filePath);
        try {
            HSSFWorkbook hssfworkbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = hssfworkbook.getSheetAt(0);
            HSSFRow firstRow = sheet.getRow(0);
            //总列数
            int rowNum = firstRow.getPhysicalNumberOfCells();
            //总行数
            cellOnRowFist = new ArrayList<>();
            for (int i = 0;i<rowNum;i++){
                String cell = getCellValue(firstRow.getCell(i));
                cellOnRowFist.add(cell);
            }
        }finally {
            inputStream.close();
        }

        return cellOnRowFist;
    }

    public static List<String> getXlsxHeader(String filePath) throws IOException {

        InputStream inputStream = new FileInputStream(filePath);
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
            XSSFRow firstRow = sheet.getRow(0);
            //第一行总列数
            int rowNum = firstRow.getPhysicalNumberOfCells();
            //总行数
            cellOnRowFist = new ArrayList<>();
            for (int i = 0; i < rowNum; i++) {
                String cell = getCellValue(firstRow.getCell(i));
                cellOnRowFist.add(cell);
            }
        }finally {
            inputStream.close();
        }
        return cellOnRowFist;
    }

    public static void xlsx2Csv(String inputFilePath,String outputFilePath) throws Exception {
        XLSX2CSV xlsx2CSV = new XLSX2CSV(inputFilePath,outputFilePath,0);
        xlsx2CSV.process();
    }

    public static void xls2Csv(String inputFilePath,String outputFilePath) throws Exception {
        XLS2CSV xls2CSV = new XLS2CSV(inputFilePath,outputFilePath,0);
        xls2CSV.process();
    }

    //xls 转为 GBK 编码的 csv
    public static void xls2CsvByGbk(String inputFilePath,String outputFilePath) throws Exception {
        XLS2CSV xls2CSV = new XLS2CSV(inputFilePath,outputFilePath,1);
        xls2CSV.process();
    }

    //xlsx 转为 GBK 编码的 csv
    public static void xlsx2CsvByGbk(String inputFilePath,String outputFilePath) throws Exception {
        XLSX2CSV xlsx2CSV = new XLSX2CSV(inputFilePath,outputFilePath,1);
        xlsx2CSV.process();
    }

    public static void xls2Xlsx(String inputFilePath,String tempFilePath,String outputFilePath) throws Exception {
        XLS2CSV xls2CSV = new XLS2CSV(inputFilePath,tempFilePath,0);
        xls2CSV.process();
        CsvUtil.csv2xlsx(tempFilePath,outputFilePath);
    }

    public static void xlsx2Xls(String inputFilePath,String tempFilePath,String outputFilePath) throws Exception {
        XLSX2CSV xlsx2CSV = new XLSX2CSV(inputFilePath,tempFilePath,0);
        xlsx2CSV.process();
        CsvUtil.csv2xls(tempFilePath,outputFilePath);
    }



    private static String getCellValue(Cell cell) {
        DecimalFormat df = new DecimalFormat("0");
        String value ;
        if (cell == null) return "";
        if (cell.getCellType() == cell.CELL_TYPE_BOOLEAN) {
            value = String.valueOf(cell.getBooleanCellValue());
            return value;
        } else if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
            value = df.format(cell.getNumericCellValue());
            return value;
        } else {
            value =  String.valueOf(cell.getStringCellValue());
            return value;
        }
    }
}
