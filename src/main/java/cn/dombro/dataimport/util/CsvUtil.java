package cn.dombro.dataimport.util;

import com.csvreader.CsvReader;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CsvUtil {


    public final static void csv2xls(String inputFilePath, String outputFilePath,int charSet) throws IOException {
        String outputCharSet = "utf-8";
        switch (charSet){
            case 0:
                outputCharSet = "utf-8";
                break;
            case 1:
                outputCharSet = "GBK";
                break;
        }
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Sheet1");
        List<String[]> csvList = new ArrayList();
        try (InputStream inputStream = new FileInputStream(inputFilePath);
             FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
            //使用 utf-8 读取 csv
            CsvReader csvReader = new CsvReader(inputStream, Charset.forName(outputCharSet));
            while (csvReader.readRecord()) {
                //获取每行的值
                csvList.add(csvReader.getValues());
            }

            for (int i = 0; i < csvList.size(); i++) {
                //创建行
                HSSFRow row = sheet.createRow(i);
                String[] values = csvList.get(i);
                for (int j = 0; j < values.length; j++) {
                    //创建单元格
                    HSSFCell cell = row.createCell(j);
                    cell.setCellValue(values[j]);
                }
            }
            //写到文件中
            wb.write(outputStream);
        }
    }


    public final static void csv2xlsx(String inputFilePath, String outputFilePath,int charSet) throws IOException {
        String outputCharSet ="utf-8";
        switch (charSet){
            case 1:
                outputCharSet = "GBK";
                break;
            case 0:
                outputCharSet = "utf-8";
                break;
        }
        XSSFWorkbook xb = new XSSFWorkbook();
        XSSFSheet sheet = xb.createSheet("Sheet1");
        List<String[]> csvList = new ArrayList();
        try (InputStream inputStream = new FileInputStream(inputFilePath);
             FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
            //使用 utf-8 读取 csv
            CsvReader csvReader = new CsvReader(inputStream, Charset.forName(outputCharSet));
            while (csvReader.readRecord()) {
                //获取每行的值
                csvList.add(csvReader.getValues());
            }


        for (int i = 0; i < csvList.size(); i++) {
            //创建行
            XSSFRow row = sheet.createRow(i);
            String[] values = csvList.get(i);
            for (int j = 0; j < values.length; j++) {
                //创建单元格
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(values[j]);
            }
        }
        //写到文件中
        xb.write(outputStream);
     }
    }



    public static List<String> getHeader(String filePath) throws IOException {
        File csvFile = new File(filePath);
        List<String[]> csvList = new ArrayList();

        try(InputStream inputStream = new FileInputStream(csvFile)){
            CsvReader csvReader = new CsvReader(inputStream,Charset.forName("UTF-8"));
            //逐行读取
            while (csvReader.readRecord() ){
                csvList.add(csvReader.getValues());
            }

        //读取后删除首行
        deleteRow(filePath,1);
         csvReader.close();
         }
        return Arrays.asList(csvList.get(0));
    }


    public static void deleteRow(String filePath,int line) throws IOException {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String str;
            List list = new ArrayList();
            int rowNum = 0;

            while((str=reader.readLine()) != null){
                ++rowNum;
                if( rowNum == line )
                    continue;
                list.add(str);
            }
            writer = new BufferedWriter(new FileWriter(filePath));
            System.out.println(list);
            for (int i = 0;i < list.size(); i++){
                writer.write(list.get(i).toString());
                writer.newLine();
            }
        }finally {
            writer.close();
            reader.close();
        }
    }
    }
