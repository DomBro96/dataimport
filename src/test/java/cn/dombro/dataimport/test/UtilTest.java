package cn.dombro.dataimport.test;


import cn.dombro.dataimport.util.*;

import it.sauronsoftware.cron4j.*;
import org.junit.Test;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


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
        System.out.println(WebTokenUtil.verifyJavaWebToken("eyJhbGciOiJIUzI1NiJ9.eyJleHBvcnRGaWxlUGF0aCI6IkU6XFxJREVBXFxkYXRhaW1wb3J0XFx0YXJnZXRcXGRhdGFpbXBvcnQtMS4wLVNOQVBTSE9UXFxkb3dubG9hZFxcc3R1ZGVudHNSc0ExMFhUaS54bHMifQ.r5MjwnKV-JgcXyC-145DJOrohyAnZuOxwTL6spYm2hI").get("exportFilePath"));
    }

    @Test
     public void  csvTest() throws Exception {
    }

    @Test
    public void dateTest(){

    }

    @Test
    public void cron4jTest(){

    }



}
