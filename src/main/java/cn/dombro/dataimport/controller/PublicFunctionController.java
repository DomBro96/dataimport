package cn.dombro.dataimport.controller;

import cn.dombro.dataimport.interceptor.AuthorizInterceptor;
import cn.dombro.dataimport.service.TaskService;
import cn.dombro.dataimport.util.ClaimUtil;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;


import java.io.File;

public class PublicFunctionController extends Controller {

    TaskService service = null;


    @Before(AuthorizInterceptor.class)
    public void downloadfile(){
        String downloadPath = (String) ClaimUtil.getValueByPara(getRequest(),"token","exportFilePath");
        System.out.println(downloadPath);
        File downloadFile = new File(downloadPath);
        service = new TaskService();
        service.startSchedule(downloadPath);
        renderFile(downloadFile);
    }




}
