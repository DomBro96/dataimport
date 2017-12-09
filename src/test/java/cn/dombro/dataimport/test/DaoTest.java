package cn.dombro.dataimport.test;



import cn.dombro.dataimport.dao.MysqlDAOImp;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DaoTest {

    @Test
    public void mysqlDaoTest() throws Exception {
//        PropKit.use("config.properties");
//        DruidPlugin dp = new DruidPlugin(PropKit.get("jdbcUrl"),PropKit.get("user"),PropKit.get("password"));
//        dp.setDriverClass(PropKit.get("driverClass"));
//        dp.set(PropKit.getInt("initialSize"),PropKit.getInt("minIdle"),PropKit.getInt("maxActive"));
//        dp.setMaxWait(PropKit.getInt("maxWait"));
//        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
//        dp.start();
//        arp.start();
//        System.out.println(dp.start());
//        System.out.println(arp.start());
//        MysqlDAOImp daoImp = new MysqlDAOImp();
//        daoImp.csvImport("C:\\Users\\18246\\Desktop\\test\\students.csv","stu");
//        daoImp.getWarning();
        //daoImp.sqlImportNoCommand("D:\\文档\\数据快速导入\\students.sql");

    }


    @Test
    public void mongoDaoTest() throws Exception {

        int mode = 2;
        List<String> fields = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\18246\\Desktop\\test\\测试1.json"))) {

            String str = reader.readLine();
            String pattern = "";
            if (mode == 1){
                pattern = "\"[a-zA-Z]*\":";
            }else if (mode == 2){
                pattern = "\"_?[a-zA-Z]*\":";
            }

            Pattern pat = Pattern.compile(pattern);
            Matcher matcher = pat.matcher(str);
            while (matcher.find()){
                    String filed =  matcher.group(0);
                    filed = filed.replaceAll("\"","").replaceAll(":","");
                    System.out.println(filed);
                    fields.add(filed);
                }
            }

        System.out.println(fields);

    }

    @Test
    public void changeFileTest(){

    }




}
