package com.jk.service.provider;
import com.jk.dao.BaseDao;
import com.jk.dao.bill.BillDao;
import com.jk.dao.bill.BillDaoImpl;
import com.jk.dao.provider.ProviderDao;
import com.jk.dao.provider.ProviderDaoImpl;
import com.jk.entity.Provider;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 * @date 2020/12/9 23 4402
 * @description
 */
public class ProviderServiceImpl implements ProviderService {
    /*创建该实现类的时候就创建对应的Dao层实现类*/
    private ProviderDao providerDao;
    private BillDao billDao;
    public ProviderServiceImpl(){
        this.providerDao = new ProviderDaoImpl();
        this.billDao = new BillDaoImpl();
    }
    @Override
    public List<Provider> getProviderList(String proCode, String proName, int currentPageNo, int pageSize) {
        Connection connection = null;
        connection = BaseDao.getConnection();
        List<Provider> providerList = null;
        try {
            providerList = providerDao.getProviderList(connection,proCode,proName,currentPageNo,pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.close(connection,null,null);
        }
        return providerList;
    }

    @Override
    public List<Provider> getAllProIdList() {
        Connection connection = null;
        List<Provider> providers = null;
        connection = BaseDao.getConnection();
        try {
            providers = providerDao.getAllProviderIdList(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BaseDao.close(connection,null,null);
        return providers;
    }

    @Override
    public int getProviderCount(String proCode, String proName) {
        Connection connection = null;
        int count = 0;
        connection = BaseDao.getConnection();
        count = providerDao.getProviderCount(connection, proCode, proName);
        BaseDao.close(connection,null,null);
        return count;
    }

    @Override
    public boolean addProvider(Provider provider) {
        boolean flag = false;
        Connection connection = null;
        connection = BaseDao.getConnection();
        int excute = 0;
        try {
            connection.setAutoCommit(false);
            excute = providerDao.addProvider(connection,provider);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }finally {
            BaseDao.close(connection,null,null);
        }
        if(excute>0){
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean providerExist(String proCode) {
        boolean flag = false;
        Connection connection = null;
        connection = BaseDao.getConnection();
        /*通过业务层来调用Dao层的代码*/
        int excute = providerDao.providerExist(connection,proCode);
        BaseDao.close(connection,null,null);
        if(excute>0){
            flag = true;
        }
        return flag;
    }
    /***
     * @函数功能：根据ID删除供应商表的数据之前，需要先去订单表里进行查询操作
     * 若订单表中无该供应商的订单数据，则可以删除
     * 若有该供应商的订单数据，则不可以删除
     * @param: providerId 供应商id
     * @return： 1删除成功，0删除失败，-1该供应商下面有订单。
     */
    @Override
    public HashMap<String,Integer> deleteProviderById(String providerId) {
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        Connection connection = null;
        int excute = 0;
        int result = -1;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);
            int i = billDao.deleteBillByProviderId(connection,Integer.valueOf(providerId));
            /*返回给Servlet然后返回给前端检验*/
            hashMap.put("billCount",i);
            /*  rusult = -1如果有订单被删除，说明该provider下有订单
            *   result = 0说明没有该provider
            *   result = 1说明有Provider 删除成功
            * */
            if(i>0){
                result = -1;
            }else{
                excute = providerDao.deleteProviderById(connection,providerId);;
                if(excute>0){
                    result = 1;
                }else {
                    result = 0;
                }
            }
            if(result==1){
                connection.commit();
            }else {
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }finally {
            BaseDao.close(connection,null,null);
        }
        /*让Servlet处理拿到的数据返回给前端去检验*/
        hashMap.put("result",result);
        return hashMap;
    }

    @Override
    public boolean modifyProvider(Provider provider) {
        boolean flag = false;
        Connection connection = null;
        connection = BaseDao.getConnection();
        int excute = 0;
        try {
            connection.setAutoCommit(false);
            excute = providerDao.modifyProvider(connection,provider);
            if(excute>0){
                connection.commit();
            }else {
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.close(connection,null,null);
        }
        if(excute>0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public Provider getProviderById(Integer providerId) {
        Connection connection = null;
        connection = BaseDao.getConnection();
        Provider provider = null;
        try {
            provider = providerDao.getProviderById(connection,providerId);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            BaseDao.close(connection,null,null);
        }
        return provider;
    }
}
