package cn.dombro.dataimport.factory;

import cn.dombro.dataimport.service.IChangeDbService;
import cn.dombro.dataimport.service.IChangeFileService;
import cn.dombro.dataimport.service.IExportDbService;
import cn.dombro.dataimport.service.IImportDbService;

public interface IServiceFactory {

    IChangeDbService getChangeDbService();
    IChangeFileService getChangeFileService();
    IExportDbService getExportDbService();
    IImportDbService getImportDbService();
}
