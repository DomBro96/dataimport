package cn.dombro.dataimport.factory;

import cn.dombro.dataimport.dao.MongodbDAO;
import cn.dombro.dataimport.dao.MongodbDAOImp;
import cn.dombro.dataimport.dao.MysqlDAO;
import cn.dombro.dataimport.dao.MysqlDAOImp;


//工厂实现类，用于创建两个DAO接口的实例
public class DAOImpFactory implements DAOFactory {

    //单例模式
    private DAOImpFactory(){

    }

    private static DAOImpFactory daoImpFactory = new DAOImpFactory();

    //饿汉单例模式
    public static DAOImpFactory getDaoImpFactory(){
         return daoImpFactory;
    }

    @Override
    public MysqlDAO getMySqlDAO() {
        return new MysqlDAOImp();
    }

    @Override
    public MongodbDAO getMongodbDAO() {
        return  new MongodbDAOImp();
    }
}
