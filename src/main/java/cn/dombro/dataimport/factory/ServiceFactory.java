package cn.dombro.dataimport.factory;

import cn.dombro.dataimport.service.*;

public class ServiceFactory implements IServiceFactory {

    private static ServiceFactory serviceFactory = new ServiceFactory();

    //单例模式
    private ServiceFactory(){

    }

    public static ServiceFactory getServiceFactory(){
        return serviceFactory;
    }

    @Override
    public IChangeDbService getChangeDbService() {
        return new ChangeDbService();
    }

    @Override
    public IChangeFileService getChangeFileService() {
        return new ChangeFileService();
    }

    @Override
    public IExportDbService getExportDbService() {
        return new ExportDbService();
    }

    @Override
    public IImportDbService getImportDbService() {
        return new ImportDbService();
    }
}
