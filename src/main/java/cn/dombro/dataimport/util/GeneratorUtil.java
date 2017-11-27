package cn.dombro.dataimport.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by 18246 on 2017/7/16.
 */
public class GeneratorUtil {



     private static int randomLen = 8;



    //字符串生成器 （8）
    public static String createRandomString(){
        int random = createRandomInt();
        return createRandomString(random,randomLen);
    }

    private static String createRandomString(int random,int len){
        Random rd = new Random(random);
        final int  maxNum = 62;
        StringBuffer sb = new StringBuffer();
        //取得随机数
        int rdGet;
        char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', 'A','B','C','D','E','F','G','H','I','J','K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y' ,'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        int count=0;
        while(count < len){
            rdGet = Math.abs(rd.nextInt(maxNum));//生成的数最大为62-1
            if (rdGet >= 0 && rdGet < str.length) {
                sb.append(str[rdGet]);
                count ++;
            }
        }
        return sb.toString();
    }

    private static int createRandomInt(){
        //得到0.0到1.0之间的数字，并扩大100000倍
        double temp = Math.random()*100000;
        //如果数据等于100000，则减少1
        if(temp>=100000){
            temp = 99999;
        }
        int tempint = (int)Math.ceil(temp);
        return tempint;
    }

    public static boolean isEngNum(String str){
        //首先,使用Pattern解释要使用的正则表达式，其中^表是字符串的开始，$表示字符串的结尾。
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z_]+$");
        //然后使用Matcher来对比目标字符串与上面解释得结果
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()){
            return true;
        }
        return false;
    }


    public static boolean readSqlFile(String uploadFilePath, String tableName) throws IOException {
        int signByTable = 0;
        int signByOther = 0;
        try(InputStreamReader reader = new InputStreamReader(new FileInputStream(uploadFilePath),"UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader)){
            String sqlLine = null;
            while ((sqlLine = bufferedReader.readLine()) != null ){
                //判断是否为非表结构,若为数据库结构直接返回 false
                if (sqlLine.startsWith("CREATE DATABASE")){
                    return false;
                }if (sqlLine.contains("CREATE TABLE `"+tableName+"` (")){
                    signByTable++;
                    //若文件中含有多个表标记加一
                }else if (sqlLine.startsWith("CREATE TABLE ") && !sqlLine.contains("CREATE TABLE `"+tableName+"` (")){
                    signByOther++;
                }
            }

            if (signByOther == 0 && signByTable == 1)
                return true;
            else
                return false;

        }
    }

}
