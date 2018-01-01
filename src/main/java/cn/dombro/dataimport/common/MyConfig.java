package cn.dombro.dataimport.common;

import cn.dombro.dataimport.controller.PublicFunctionController;
import cn.dombro.dataimport.controller.SystemFunctionController;
import cn.dombro.dataimport.service.TaskService;
import cn.dombro.dataimport.util.CmdUtil;
import cn.dombro.dataimport.util.FilePathEnum;
import com.jfinal.config.*;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import java.io.File;
import com.jfinal.template.Engine;

public class MyConfig extends JFinalConfig {
    public void configConstant(Constants constants) {
      PropKit.use("config.properties");
      //文件上传路径
        constants.setBaseUploadPath(FilePathEnum.UPLOAD_PATH.getFilePath());
        constants.setMaxPostSize(20*1024*1024);
      constants.setDevMode(true);

    }

    public void configRoute(Routes routes) {
        //系统功能模块路由
        routes.add("/sf", SystemFunctionController.class,null);
        //公共功能模块路由
        routes.add("/pf", PublicFunctionController.class,null);
    }

    public void configEngine(Engine engine) {

    }

    public void configPlugin(Plugins plugins) {
        DruidPlugin dp = new DruidPlugin(PropKit.get("jdbcUrl"),PropKit.get("user"),PropKit.get("password"));
        dp.setDriverClass(PropKit.get("driverClass"));
        dp.set(PropKit.getInt("initialSize"),PropKit.getInt("minIdle"),PropKit.getInt("maxActive"));
        dp.setMaxWait(PropKit.getInt("maxWait"));
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
        plugins.add(dp);
        plugins.add(arp);
    }

    public void configInterceptor(Interceptors interceptors) {

    }

    public void configHandler(Handlers handlers) {

    }

    @Override
    public void afterJFinalStart() {
        File[] files = {new File(FilePathEnum.UPLOAD_PATH.getFilePath()),
                        new File(FilePathEnum.TEMP_PATH.getFilePath()),
                        new File(FilePathEnum.DOWNLOAD_PATH.getFilePath())};
        for (int i = 0;i<files.length;i++){
            if (! files[i].exists()) {
                files[i].mkdir();
            }
            if (!System.getProperty("os.name").startsWith("Windows")){
                CmdUtil.linuxCmd("chmod 777 "+files[i]);
            }
        }
        //项目启动启动调度
        TaskService taskService = new TaskService();
        taskService.startSchedule();
    }
}
