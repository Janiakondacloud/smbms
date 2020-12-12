package com.jk.dao.bill;
import com.jk.dao.BaseDao;
import com.jk.entity.Bill;
import com.mysql.jdbc.StringUtils;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Administrator
 * @date 2020/12/10 16 1526
 * @description 在Dao完成增删改查操作，不管理连接。Service层管理连接，管理事务操作。
 */
public class BillDaoImpl implements BillDao {
    @Override
    public int getBillCount(Connection connection, String productName, Integer providerId,Integer ispayment) {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        ArrayList<String> list = new ArrayList<String>();
        StringBuffer sql = new StringBuffer();
        int billCount = 0;
        if(connection!=null){
            sql.append("select count(1) as count from smbms_bill as b,smbms_provider as p where " +
                    "b.providerId = p.id");
            if(!StringUtils.isNullOrEmpty(productName)){
                sql.append(" and b.productName like ?");
                list.add("%"+productName+"%");
            }
            if(providerId>0){
                sql.append(" and b.providerId = ?");
                list.add(String.valueOf(providerId));
            }
            if(ispayment>0){
                sql.append(" and b.isPayment = ?");
                list.add(String.valueOf(ispayment));
            }
            Object[] params = list.toArray();
            try {
                rs = BaseDao.execute(connection,sql.toString(),params,rs,preparedStatement);
                if(rs.next()){
                    billCount = rs.getInt("count");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                BaseDao.close(null,rs,preparedStatement);
            }
        }
        return billCount;
    }

    @Override
    public List<Bill> getBillList(Connection connection, String productName, Integer providerId,Integer isPayment,int currentPageNo, int pageSize) throws Exception {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<Bill> bills = new ArrayList<Bill>();
        if(connection!=null){
            StringBuffer sql = new StringBuffer();
            sql.append("select b.*,p.proName as providerName from smbms_bill b,smbms_provider p where b.providerId = p.id");
            ArrayList<Object> lists = new ArrayList<>();
            if(!StringUtils.isNullOrEmpty(productName)){
                sql.append(" and b.productName like ?");
                lists.add("%"+productName+"%");
            }
            if(providerId>0){
                sql.append(" and b.providerId = ?");
                lists.add(String.valueOf(providerId));
            }
            if(isPayment>0){
                sql.append(" and b.isPayment = ?");
                lists.add(String.valueOf(isPayment));
            }
            sql.append(" order by creationDate DESC limit ?,?");
            /*分页很关键的一步*/
            currentPageNo = (currentPageNo-1)*pageSize;
            lists.add(currentPageNo);
            lists.add(pageSize);
            Object[] params = lists.toArray();
            System.out.println("sql为"+sql.toString());
            resultSet = BaseDao.execute(connection,sql.toString(),params,resultSet,preparedStatement);
            while (resultSet.next()){
                Bill bill = new Bill();
                bill.setId(resultSet.getInt("id"));
                bill.setBillCode(resultSet.getString("billCode"));
                bill.setProductName(resultSet.getString("productName"));
                bill.setProductDesc(resultSet.getString("productDesc"));
                bill.setProductUnit(resultSet.getString("productUnit"));
                bill.setProductCount(resultSet.getBigDecimal("productCount"));
                bill.setTotalPrice(resultSet.getBigDecimal("totalPrice"));
                bill.setIsPayment(resultSet.getInt("isPayment"));
                bill.setCreateBy(resultSet.getInt("createdBy"));
                bill.setModifyBy(resultSet.getInt("modifyBy"));
                bill.setCreationDate(resultSet.getDate("creationDate"));
                bill.setModifyDate(resultSet.getDate("modifyDate"));
                bill.setProviderId(resultSet.getInt("providerId"));
                bill.setProviderName(resultSet.getString("providerName"));
                bills.add(bill);
            }
            BaseDao.close(null,resultSet,preparedStatement);
        }
        return bills;

    }

    @Override
    public int addBill(Connection connection, Bill bill) {
        PreparedStatement preparedStatement = null;
        int execute = 0;
        if(connection!=null){
            String sql = "insert into smbms_bill ( billCode, productName," +
                    " productDesc, productUnit, productCount, totalPrice, isPayment, createdBy, creationDate, providerId)"+
                    "values (?,?,?,?,?,?,?,?,?,?);";
            String billCode = bill.getBillCode();
            String productName = bill.getProductName();
            /*默认商品描述*/
            String productDesc = "好用的很";
            String productUnit = bill.getProductUnit();
            BigDecimal productCount = bill.getProductCount();
            BigDecimal totalPrice = bill.getTotalPrice();
            Integer isPayment = bill.getIsPayment();
            /*默认是1 系统管理员创建*/
            Integer createdBy = 1;
            Integer providerId = bill.getProviderId();
            /*创建的时候的Date*/
            Date creationDate = new Date();
            Object params[] = {billCode,productName,productDesc,productUnit,productCount,totalPrice,isPayment,createdBy,creationDate,providerId};
            try {
                execute = BaseDao.execute(connection,sql,params,preparedStatement);
                BaseDao.close(null,null,preparedStatement);//链接不关
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return execute;
    }

    @Override
    public boolean billExist(Connection connection, String billCode) {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        boolean flag = false;
        if(connection!=null){
            String sql = "select * from smbms_bill where billCode = ?";
            Object params[] = {billCode};
            try {
                rs = BaseDao.execute(connection,sql,params,rs,preparedStatement);
                BaseDao.close(null,null,preparedStatement);//链接不关
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if(rs.next()){
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }
    @Override
    public int deleteBillById(Connection connection, Integer billId) {
        PreparedStatement preparedStatement = null;
        int execute = 0;
        if(connection!=null){
            String sql = "delete from smbms_bill where id = ?";
            Object params[] = {billId};
            try {
                execute = BaseDao.execute(connection,sql,params,preparedStatement);
                BaseDao.close(null,null,preparedStatement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return execute;
    }
    @Override
    public int deleteBillByProviderId(Connection connection, Integer providerId) {
        PreparedStatement preparedStatement = null;
        int execute = 0;
        if(connection!=null){
            String sql = "delete from smbms_bill where providerId = ?";
            Object params[] = {providerId};
            try {
                execute = BaseDao.execute(connection,sql,params,preparedStatement);
                BaseDao.close(null,null,preparedStatement);//链接不关
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return execute;
    }


    @Override
    public int modifyBill(Connection connection, Bill bill) throws Exception{
        PreparedStatement preparedStatement = null;
        int execute = 0;
        if(connection!=null){
            Integer billId = bill.getId();
            String billCode = bill.getBillCode();
            String productName = bill.getProductName();
            String productUnit = bill.getProductUnit();
            BigDecimal productCount = bill.getProductCount();
            BigDecimal totalPrice = bill.getTotalPrice();
            Integer providerId = bill.getProviderId();
            Integer isPayment = bill.getIsPayment();
            if(StringUtils.isNullOrEmpty(billCode)){
                billCode = "";
            }
            if(StringUtils.isNullOrEmpty(productName)){
                productName = "";
            }
            if(StringUtils.isNullOrEmpty(productUnit)){
                productUnit = "";
            }
            /*创建修改的时间*/
            Date modifyDate = new Date();
            String sql = " update smbms_bill set billCode = ?,productName = ?,productUnit = ?," +
                    " productCount = ?,isPayment = ?,totalPrice = ?,modifyDate = ?,providerId = ? where id = ?";
            Object params[] = {billCode,productName,productUnit,productCount,isPayment,totalPrice,modifyDate,providerId,billId};
            try {
                execute = BaseDao.execute(connection,sql,params,preparedStatement);
                BaseDao.close(null,null,preparedStatement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return execute;
    }

    @Override
    public Bill getBillById(Connection connection, Integer billId) throws SQLException {
        Bill bill = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        if(null != connection){
            String sql = "select b.*,p.proName as providerName from smbms_bill b,smbms_provider p where" +
                    " b.id=? and b.providerId = p.id";
            Object[] params = {billId};
            rs = BaseDao.execute(connection, sql,params,rs,pstm);
            if(rs.next()){
                bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setProviderId(rs.getInt("providerId"));
                bill.setProviderName(rs.getString("providerName"));
                bill.setBillCode(rs.getString("billCode"));
                bill.setProductName(rs.getString("productName"));
                bill.setProductDesc(rs.getString("productDesc"));
                bill.setProductUnit(rs.getString("productUnit"));
                bill.setProductCount(rs.getBigDecimal("productCount"));
                bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
                bill.setIsPayment(rs.getInt("isPayment"));
                bill.setModifyBy(rs.getInt("modifyBy"));
                bill.setCreateBy(rs.getInt("createdBy"));
                bill.setCreationDate(rs.getDate("creationDate"));
                bill.setModifyDate(rs.getDate("modifyDate"));
            }
            BaseDao.close(null,rs,pstm);
        }
        return bill;
    }
}
