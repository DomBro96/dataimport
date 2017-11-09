package cn.dombro.dataimport.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class CmdUtil {


    public static Object linuxCmd(String cmd){
        try{
            String[] cmds = {"/bin/bash","-c",cmd};
            Process process = Runtime.getRuntime().exec(cmds);
            LineNumberReader br = new LineNumberReader(new InputStreamReader(process.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while((line = br.readLine()) != null){
                System.out.println(line);
                sb.append(line).append("\n");
            }
            return sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void windowsCmd(String cmd) throws IOException {
        BufferedReader bufferedReader = null;
        try {
            //在一个单独的进程中执行指定的字符串命令
            String[] cmds = {"cmd.exe","/c",cmd};
            Process process = Runtime.getRuntime().exec(cmds);
            //process.getInputStream()获取 子进程 的输出流，该输出流作为被传送给 该Process 对象表示的进程输入流
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line + "\n");
            }
            System.out.println(stringBuilder.toString());
        }finally {
            if (bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
