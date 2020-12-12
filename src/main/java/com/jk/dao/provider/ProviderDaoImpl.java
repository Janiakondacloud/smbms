package com.jk.dao.provider;

import com.jk.dao.BaseDao;
import com.jk.entity.Provider;
import com.mysql.jdbc.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Administrator
 * @date 2020/12/9 21 5654
 * @description
 */
public class ProviderDaoImpl implements ProviderDao{
    @Override
    public List<Provider> getProviderList(Connection connection,String queryProCode,String queryProName, int currentPageNo, int pageSize) throws Exception{
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<Provider> providers = new ArrayList<Provider>();
        if(connection!=null){
            StringBuffer sql = new StringBuffer();
            /*这里写where 1 = 1是为了方便实现动态SQL*/
            sql.append("select * from smbms_provider where 1=1");
            ArrayList<Object> lists = new ArrayList<>();
            if(!StringUtils.isNullOrEmpty(queryProCode)){
                sql.append(" and proCode like ?");
                lists.add("%"+queryProCode+"%");
            }
            if(!StringUtils.isNullOrEmpty(queryProName)){
                sql.append(" and proName like ?");
                lists.add("%"+queryProName+"%");
            }
            /*最后一个参数为页面记录个数，倒数第二个为第几页*/
            sql.append(" order by creationDate DESC limit ?,?");
            /* 10 = （3-1）*5   前记录的起始位置在第10个
            * order by creationDate DESC limit 10,5 意思从第十个开始往后面取5个，完成分页操作。
            * */
            currentPageNo = (currentPageNo-1)*pageSize;
            lists.add(currentPageNo);
            lists.add(pageSize);
            Object[] params = lists.toArray();
            resultSet = BaseDao.execute(connection,sql.toString(),params,resultSet,preparedStatement);
            while (resultSet.next()){
               Provider provider = new Provider();
               provider.setId(resultSet.getInt("id"));
               provider.setProCode(resultSet.getString("proCode"));
               provider.setProName(resultSet.getString("proName"));
               provider.setProDesc(resultSet.getString("proDesc"));
               provider.setProContact(resultSet.getString("ProContact"));
               provider.setProPhone(resultSet.getString("proPhone"));
               provider.setProAddress(resultSet.getString("proAddress"));
               provider.setProFax(resultSet.getString("proFax"));
               provider.setCreateBy(resultSet.getInt("createdBy"));
               provider.setCreationDate(resultSet.getDate("creationDate"));
               provider.setModifyDate(resultSet.getDate("modifyDate"));
               provider.setModifyBy(resultSet.getInt("modifyBy"));
               providers.add(provider);
            }
            BaseDao.close(null,resultSet,preparedStatement);
        }
        return providers;
    }

    @Override
    public List<Provider> getAllProviderIdList(Connection connection) throws Exception {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<Provider> providers = new ArrayList<Provider>();
        if(connection!=null){
            String sql = "select id,ProCode,ProName from smbms_provider where 1 = ?";
            Object[] params = {1};
            resultSet = BaseDao.execute(connection,sql.toString(),params,resultSet,preparedStatement);
            while (resultSet.next()){
                Provider provider = new Provider();
                provider.setId(resultSet.getInt("id"));
                provider.setProCode(resultSet.getString("proCode"));
                provider.setProName(resultSet.getString("proName"));
                providers.add(provider);
            }
            BaseDao.close(null,resultSet,preparedStatement);
        }
        return providers;
    }

    @Override
    public int addProvider(Connection connection, Provider provider) {
        PreparedStatement preparedStatement = null;
        int execute = 0;
        if(connection!=null){
            String sql = "insert into smbms_provider (proCode, proName," +
                    " proDesc, proContact, proPhone, proAddress, proFax, creationDate)"+
                    "values (?,?,?,?,?,?,?,?);";
            String proCode = provider.getProCode();
            String proName = provider.getProName();
            String proContact = provider.getProContact();
            String proPhone = provider.getProPhone();
            String proAddress = provider.getProAddress();
            String proFax = provider.getProFax();
            String proDesc = provider.getProDesc();
            /*这里获取系统当前日期作为创建日期*/
            Date creationDate = new Date();
            if(StringUtils.isNullOrEmpty(proCode)){
                proCode = "";
            }
            if(StringUtils.isNullOrEmpty(proFax)){
                proFax = "";
            }
            if(StringUtils.isNullOrEmpty(proDesc)){
                proDesc = "";
            }
            if(StringUtils.isNullOrEmpty(proName)){
                proName = "";
            }
            if(StringUtils.isNullOrEmpty(proContact)){
                proContact = "";
            }
            if(StringUtils.isNullOrEmpty(proPhone)){
                proPhone = "";
            }
            if(StringUtils.isNullOrEmpty(proAddress)){
                proAddress = "";
            }
            Object params[] = {proCode,proName,proDesc,proContact,proPhone,proAddress,proFax,creationDate};
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
    public int providerExist(Connection connection, String proCode) {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        int flag = 0;
        if(connection!=null){
            String sql = "select * from smbms_provider where proCode = ?";
            Object params[] = {proCode};
            try {
                rs = BaseDao.execute(connection,sql,params,rs,preparedStatement);
                BaseDao.close(null,null,preparedStatement);//链接不关
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if(rs.next()){
                flag = 1;
            }else {
                flag = 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public int deleteProviderById(Connection connection, String providerId) {
        PreparedStatement preparedStatement = null;
        int execute = 0;
        if(connection!=null){
            String sql = "delete from smbms_provider where id = ?";
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
    public int modifyProvider(Connection connection, Provider provider) throws Exception {
        PreparedStatement preparedStatement = null;
        int execute = 0;
        if(connection!=null){
            String proCode = provider.getProCode();
            String proName = provider.getProName();
            String proContact = provider.getProContact();
            String proPhone = provider.getProPhone();
            String proAddress = provider.getProAddress();
            String proFax = provider.getProFax();
            String proDesc = provider.getProDesc();
            Integer id = provider.getId();
            /*获取修改的日期*/
            Date modifyDate = new Date();
            if(StringUtils.isNullOrEmpty(proCode)){
                proCode = "";
            }
            if(StringUtils.isNullOrEmpty(proFax)){
                proFax = "";
            }
            if(StringUtils.isNullOrEmpty(proDesc)){
                proDesc = "";
            }
            if(StringUtils.isNullOrEmpty(proName)){
                proName = "";
            }
            if(StringUtils.isNullOrEmpty(proContact)){
                proContact = "";
            }
            if(StringUtils.isNullOrEmpty(proPhone)){
                proPhone = "";
            }
            if(StringUtils.isNullOrEmpty(proAddress)){
                proAddress = "";
            }
            String sql = "update smbms_provider set proCode = ?,proName = ?,proContact = ?," +
                    "proPhone = ?,proAddress = ?,proFax = ?,proDesc = ?,modifyDate = ? where id = ?";
            Object params[] = {proCode,proName,proContact,proPhone,proAddress,proFax,proDesc,modifyDate,id};
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
    public Provider getProviderById(Connection connection, Integer providerId) throws SQLException {
        Provider provider = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        if(null != connection){
            String sql = "select * from smbms_provider where id = ?";
            Object[] params = {providerId};
            rs = BaseDao.execute(connection, sql,params,rs,pstm);
            if(rs.next()){
                provider = new Provider();
                provider.setId(rs.getInt("id"));
                provider.setProCode(rs.getString("ProCode"));
                provider.setProName(rs.getString("ProName"));
                provider.setProDesc(rs.getString("ProDesc"));
                provider.setProContact(rs.getString("proContact"));
                provider.setProPhone(rs.getString("proPhone"));
                provider.setProAddress(rs.getString("proAddress"));
                provider.setProFax(rs.getString("proFax"));
                provider.setCreateBy(rs.getInt("createdBy"));
                provider.setCreationDate(rs.getDate("creationDate"));
                provider.setModifyBy(rs.getInt("modifyBy"));
                provider.setModifyDate(rs.getDate("modifyDate"));
            }
            BaseDao.close(null,rs,pstm);
        }
        return provider;
    }

    @Override
    public int getProviderCount(Connection connection, String proCode, String proName) {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        int count = 0;
        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_provider as p where 1=1 ");
            ArrayList<Object> lists = new ArrayList<>();
            if (!StringUtils.isNullOrEmpty(proName)) {
                sql.append(" and p.proName like ?");
                lists.add("%" + proName + "%");
            }
            if (!StringUtils.isNullOrEmpty(proCode)) {
                sql.append(" and p.proCode like ?");
                lists.add("%"+proCode+"%");
            }
            System.out.println(lists);
            Object[] params = lists.toArray();
            try {
                rs = BaseDao.execute(connection, sql.toString(), params, rs, preparedStatement);
                if (rs.next()) {
                    count = rs.getInt("count");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            BaseDao.close(null, rs, preparedStatement);
        }
        return count;
    }
}

