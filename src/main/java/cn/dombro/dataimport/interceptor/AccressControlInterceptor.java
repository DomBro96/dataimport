package cn.dombro.dataimport.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

import javax.servlet.http.HttpServletResponse;

public class AccressControlInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation invocation) {

        HttpServletResponse response = invocation.getController().getResponse();
        response.setHeader("Access-Control-Allow-Origin", "*");
        invocation.invoke();
    }
}
