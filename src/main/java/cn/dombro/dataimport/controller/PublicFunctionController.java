package cn.dombro.dataimport.controller;

import cn.dombro.dataimport.interceptor.AccressControlInterceptor;
import cn.dombro.dataimport.interceptor.AuthorizInterceptor;
import cn.dombro.dataimport.service.TaskService;
import cn.dombro.dataimport.util.ClaimUtil;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;


import java.io.File;

@Before(AccressControlInterceptor.class)
public class PublicFunctionController extends Controller {


    @Before(AuthorizInterceptor.class)
    public void downloadfile(){
        String downloadPath = (String) ClaimUtil.getValueByPara(getRequest(),"token","exportFilePath");
        System.out.println(downloadPath);
        File downloadFile = new File(downloadPath);
        renderFile(downloadFile);
    }




}
