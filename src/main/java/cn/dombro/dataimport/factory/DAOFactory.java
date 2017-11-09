package cn.dombro.dataimport.factory;

import cn.dombro.dataimport.dao.MongodbDAO;
import cn.dombro.dataimport.dao.MysqlDAO;

public interface DAOFactory {

    MysqlDAO getMySqlDAO();

    MongodbDAO getMongodbDAO();
}
