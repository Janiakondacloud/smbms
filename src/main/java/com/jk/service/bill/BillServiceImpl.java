package com.jk.service.bill;
import com.jk.dao.BaseDao;
import com.jk.dao.bill.BillDao;
import com.jk.dao.bill.BillDaoImpl;
import com.jk.entity.Bill;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Administrator
 * @date 2020/12/9 23 4402
 * @description 实现类 实现事务管理
 */
public class BillServiceImpl implements BillService {
    /*开头的两行非常关键，在创建该实现类的对象的时候创建对应Dao实现类*/
    private BillDao billDao;
    public BillServiceImpl(){
        this.billDao = new BillDaoImpl();
    }
    @Override
    public List<Bill> getBillList(String productName, Integer providerId, Integer isPayment,int currentPageNo, int pageSize) {
        Connection connection = null;
        connection = BaseDao.getConnection();
        List<Bill> billList = null;
        try {
            billList = billDao.getBillList(connection,productName,providerId,isPayment,currentPageNo,pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.close(connection,null,null);
        }
        return billList;
    }

    @Override
    public int getBillCount(String productName, Integer providerId, Integer isPayment) {
        Connection connection = null;
        int count = 0;
        connection = BaseDao.getConnection();
        count = billDao.getBillCount(connection, productName, providerId,isPayment);
        BaseDao.close(connection,null,null);
        return count;
    }

    @Override
    public boolean addBill(Bill bill) {
        boolean flag = false;
        Connection connection = null;
        connection = BaseDao.getConnection();
        int excute = 0;
        try {
            /*自动提交事务关闭则开启事务*/
            connection.setAutoCommit(false);
            excute = billDao.addBill(connection,bill);
            /*成功则提交*/
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                /*如果添加过程中异常则回滚*/
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
    public boolean billExist(String billCode) {
        boolean flag = false;
        Connection connection = null;
        connection = BaseDao.getConnection();
        /*通过业务层来调用Dao层的代码*/
        flag = billDao.billExist(connection,billCode);
        BaseDao.close(connection,null,null);
        return flag;
    }
    @Override
    public boolean deleteBillById(String billId) {
        boolean flag = false;
        Connection connection = null;
        int excute = 0;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);
            excute = billDao.deleteBillById(connection,Integer.valueOf(billId));
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
    /***
     * @函数功能：根据供应商id删除订单，判断该供应商下是否有订单。
     * @param: providerId 供应商id
     * @return：大于0有订单，等于0没有订单。
     */
    @Override
    public int deleteBillByProviderId(Integer providerId) {
        Connection connection = null;
        int excute = 0;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);
            excute = billDao.deleteBillByProviderId(connection,providerId);
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
        return excute;
    }

    @Override
    public boolean modifyBill(Bill bill) {
        boolean flag = false;
        Connection connection = null;
        connection = BaseDao.getConnection();
        int excute = 0;
        try {
            connection.setAutoCommit(false);
            excute = billDao.modifyBill(connection,bill);
            connection.commit();
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
        if(excute>0){
            flag = true;
        }
        return flag;
    }

    @Override
    public Bill getBillById(Integer billId) {
        Connection connection = null;
        connection = BaseDao.getConnection();
        Bill bill = null;
        try {
            bill = billDao.getBillById(connection,billId);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.close(connection,null,null);
        }
        return bill;
    }
}

