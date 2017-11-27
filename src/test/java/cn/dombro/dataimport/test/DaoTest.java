package cn.dombro.dataimport.test;



import cn.dombro.dataimport.dao.MysqlDAOImp;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import org.junit.Test;


public class DaoTest {

    @Test
    public void mysqlDaoTest() throws Exception {
        PropKit.use("config.properties");
        DruidPlugin dp = new DruidPlugin(PropKit.get("jdbcUrl"),PropKit.get("user"),PropKit.get("password"));
        dp.setDriverClass(PropKit.get("driverClass"));
        dp.set(PropKit.getInt("initialSize"),PropKit.getInt("minIdle"),PropKit.getInt("maxActive"));
        dp.setMaxWait(PropKit.getInt("maxWait"));
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        dp.start();
        arp.start();
        System.out.println(dp.start());
        System.out.println(arp.start());
        MysqlDAOImp daoImp = new MysqlDAOImp();
//        daoImp.sqlImportNoCommand("D:\\文档\\数据快速导入\\students.sql");

    }


    @Test
    public void mongoDaoTest() throws Exception {

    }

    @Test
    public void changeFileTest(){

    }




}
