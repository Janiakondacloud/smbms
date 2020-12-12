package com.jk.dao.bill;
import com.jk.entity.Bill;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Administrator
 * @date 2020/12/10 16 1512
 * @description
 */
public interface BillDao {
    //根据订单编号或者名字找到对应的供应商总数
    public int getBillCount(Connection connection, String productName, Integer providerId,Integer isPayment);
    //获取订单列表
    public List<Bill> getBillList(Connection connection, String productName, Integer providerId,Integer isPayment,int currentPageNo, int pageSize) throws Exception;
    //添加订单
    public int addBill(Connection connection, Bill bill);
    //判断订单是否存在
    public boolean billExist(Connection connection, String billCode);
    //删除根据供应商信息删除订单
    public int deleteBillById(Connection connection,Integer billId);
    //这里用作删除供应商的时候确认有没有订单，如果该供应商下有订单则不能删除。
    public int deleteBillByProviderId(Connection connection, Integer providerId);
    //修改订单
    public int modifyBill(Connection connection,Bill bill) throws Exception;
    //查看订单信息
    public Bill getBillById(Connection connection, Integer billId) throws SQLException;
}
