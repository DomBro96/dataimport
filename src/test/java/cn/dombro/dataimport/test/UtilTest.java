package cn.dombro.dataimport.test;


import cn.dombro.dataimport.util.*;

import it.sauronsoftware.cron4j.*;
import org.junit.Test;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class UtilTest {

    @Test
   public void ExcelUtilTest() throws IOException {
   }

   @Test
   public void CsvUtilTest(){
   }

    @Test
    public void filePathTest() throws IOException {
    }


    @Test
    public void mongoTest(){
     }

    @Test
    public void patternTest(){
    }

    @Test
    public void tokenTest(){
        //System.out.println(WebTokenUtil.verifyJavaWebToken("eyJhbGciOiJIUzI1NiJ9.eyJleHBvcnRGaWxlUGF0aCI6IkU6XFxJREVBXFxkYXRhaW1wb3J0XFx0YXJnZXRcXGRhdGFpbXBvcnQtMS4wLVNOQVBTSE9UXFxkb3dubG9hZFxcc3R1ZGVudHNSc0ExMFhUaS54bHMifQ.r5MjwnKV-JgcXyC-145DJOrohyAnZuOxwTL6spYm2hI").get("exportFilePath"));
    }

    @Test
     public void  csvTest() throws Exception {

        String testString = "name,varchar,20,pk";
        String[] testArray = testString.split(",");
        System.out.println(testArray.length);
        for (String test:testArray){
             System.out.println(test);
        }
    }

    @Test
    public void dateTest(){

//        List<String> fields = new ArrayList<>();
//
//        fields.add("phone,int,20");
//        fields.add("name,varchar,20");
//        fields.add("number,varchar,20");
//        fields.add("grade,varchar,20");
//
//        String sql = "CREATE TABLE "+"students"
//                +"( ";
//        List<String> pkList = new ArrayList<>();
//        if (fields != null && fields.size() > 0){
//            for (int i = 0;i < fields.size();i++){
//                String[] mappingArray = fields.get(i).split(",");
//                sql += mappingArray[0] + " " + mappingArray[1]+"("+mappingArray[2]+")";
//                if (mappingArray.length == 4 && mappingArray[3].equals("pk")){
//                    pkList.add(mappingArray[0]);
//                }
//                //拼接逗号
//                if (i < fields.size() - 1){
//                    sql += ",";
//                }
//            }
//        }
//
//        //设置默认字符集
//        if (pkList.size() != 0) {
//            sql += " ,PRIMARY KEY (";
//            for (int j = 0; j < pkList.size(); j++) {
//                sql += pkList.get(j);
//                if (pkList.size() != 1 && j < pkList.size() - 1) {
//                    sql += ",";
//                }
//                if (j == pkList.size()-1){
//                    sql += ")";
//                }
//            }
//        }
//        sql += " );";
//        System.out.println(sql);
    }

    @Test
    public void cron4jTest(){

    }



}
