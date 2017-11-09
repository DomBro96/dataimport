package cn.dombro.dataimport.interceptor;

import cn.dombro.dataimport.util.ClaimUtil;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import java.io.File;

public class AuthorizInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation invocation) {
        Controller controller = invocation.getController();
        String downloadPath = (String) ClaimUtil.getValueByPara(controller.getRequest(),"token","exportFilePath");
        System.out.println("dbb"+downloadPath);
        File file = new File(downloadPath);
        if (!file.exists())
            controller.renderJson("authorization","T001");
        else
            //controller.renderJson("authorization","T000");
            invocation.invoke();
    }
}
