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
import org.jfree.report.util.CSVTokenizer;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CsvUtil {


        public final static void csv2xls(String inputFilePath, String outputFilePath) throws IOException
        {
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("Sheet1");
            BufferedReader r = null;
            try
            {
                r = new BufferedReader(new FileReader(inputFilePath));

                int i = 0;

                while (true)
                {
                    String ln = r.readLine();

                    if (ln == null)
                        break;

                    HSSFRow row = sheet.createRow((short) i++);
                    int j = 0;

                    for (CSVTokenizer it = new CSVTokenizer(ln); it.hasMoreTokens();)
                    {
                        String val = it.nextToken();

                        HSSFCell cell = row.createCell((short) j++);
                        cell.setCellValue(val);
                    }
                }
            }
            finally
            {
                if (r != null)
                    r.close();
            }

            FileOutputStream fileOut = null;
            try
            {
                fileOut = new FileOutputStream(outputFilePath);
                wb.write(fileOut);
            }
            finally
                    {
                    fileOut.close();
                    }
        }



    public final static void csv2xlsx(String inputFilePath, String outputFilePath) throws IOException {
        XSSFWorkbook xb = new XSSFWorkbook();
        XSSFSheet sheet = xb.createSheet("Sheet1");

        BufferedReader r = null;

        try {
            r = new BufferedReader(new FileReader(inputFilePath));

            int i = 0;

            while (true) {
                String ln = r.readLine();

                if (ln == null)
                    break;

                XSSFRow row = sheet.createRow((short) i++);
                int j = 0;

                for (CSVTokenizer it = new CSVTokenizer(ln); it.hasMoreTokens(); ) {
                    String val = it.nextToken();

                    XSSFCell cell = row.createCell((short) j++);
                    cell.setCellValue(val);
                }
            }
        } finally {
            if (r != null)
                r.close();
        }

        FileOutputStream fileOut = null;

        try {
            fileOut = new FileOutputStream(outputFilePath);
            xb.write(fileOut);
        } finally {
            fileOut.close();
        }
    }

    public static List<String> getHeader(String filePath) throws IOException {
               File csvFile = new File(filePath);
        List<String[]> csvList = new ArrayList<String[]>();
        InputStream inputStream = new FileInputStream(csvFile);
        CsvReader csvReader = new CsvReader(inputStream,Charset.forName("UTF-8"));

        //逐行读取
        while (csvReader.readRecord()){
                csvList.add(csvReader.getValues());
            }
        csvReader.close();
        return Arrays.asList(csvList.get(0));
    }


    }
