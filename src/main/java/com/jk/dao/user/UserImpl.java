package com.jk.dao.user;

import com.jk.dao.BaseDao;

import com.jk.entity.User;
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
 * @date 2020/12/6 15 1541
 * @description
 */
public class UserImpl implements UserDao{
    @Override
    public User getLoginUser(Connection connection, String userCode,String userPassword) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
        if(connection!=null){
            String sql = "select * from smbms_user where userCode = ? and userPassword = ?";
            Object[] params = {userCode,userPassword};
            try {
                preparedStatement = connection.prepareStatement(sql);
                resultSet = BaseDao.execute(connection,sql,params,null,preparedStatement);
                if(resultSet.next()){
                    user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setUserCode(resultSet.getString("userCode"));
                    user.setUserName(resultSet.getString("userName"));
                    user.setUserPassword(resultSet.getString("userPassword"));
                    user.setGender(resultSet.getInt("gender"));
                    user.setBirthday(resultSet.getDate("birthday"));
                    user.setPhone(resultSet.getString("phone"));
                    user.setAddress(resultSet.getString("address"));
                    user.setUserRole(resultSet.getInt("userRole"));
                    user.setCreateBy(resultSet.getInt("createdBy"));
                    user.setModifyBy(resultSet.getInt("modifyBy"));
                    user.setCreationDate(resultSet.getTimestamp("creationDate"));
                    user.setModifyDate(resultSet.getTimestamp("modifyDate"));
                }
                BaseDao.close(null,resultSet,preparedStatement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    @Override
    public boolean updatePwd(Connection connection, String userCode, String userPassword) {
        PreparedStatement preparedStatement = null;
        int execute = 0;
        if(connection!=null){
            String sql = "update smbms_user set userPassword = ?,modifyDate = ? where userCode = ?";
            Date modifyDate = new Date();
            Object params[] = {userPassword,modifyDate,userCode};
            try {
                execute = BaseDao.execute(connection,sql,params,preparedStatement);
                BaseDao.close(null,null,preparedStatement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(execute>0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public int getUserCount(Connection connection, String userName, int userRole) {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        int count = 0;
        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_user u,smbms_role r where u.userRole = r.id");
            ArrayList<Object> lists = new ArrayList<>();
            if (!StringUtils.isNullOrEmpty(userName)) {
                sql.append(" and u.userName like ?");
                lists.add("%" + userName + "%");
            }
            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                lists.add(userRole);
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

    @Override
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws Exception {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<User> users = new ArrayList<>();
        if(connection!=null){
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.userRole = r.id");
            ArrayList<Object> lists = new ArrayList<>();
            if(!StringUtils.isNullOrEmpty(userName)){
                sql.append(" and u.userName like ?");
                lists.add("%"+userName+"%");
            }
            if(userRole>0){
                sql.append(" and r.id = ?");
                lists.add(userRole);
            }
            sql.append(" order by creationDate DESC limit ?,?");
            currentPageNo = (currentPageNo-1)*pageSize;
            lists.add(currentPageNo);
            lists.add(pageSize);
            Object[] params = lists.toArray();
            resultSet = BaseDao.execute(connection,sql.toString(),params,resultSet,preparedStatement);
            while (resultSet.next()){
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUserCode(resultSet.getString("userCode"));
                user.setUserName(resultSet.getString("userName"));
                user.setGender(resultSet.getInt("gender"));
                user.setBirthday(resultSet.getDate("birthday"));
                user.setPhone(resultSet.getString("phone"));
                user.setUserRole(resultSet.getInt("userRole"));
                user.setCreateBy(resultSet.getInt("createdBy"));
                user.setModifyBy(resultSet.getInt("modifyBy"));
                user.setCreationDate(resultSet.getTimestamp("creationDate"));
                user.setModifyDate(resultSet.getTimestamp("modifyDate"));
                user.setUserRoleName(resultSet.getString("userRoleName"));
                users.add(user);
            }
            BaseDao.close(null,resultSet,preparedStatement);
        }
        return users;
    }

    @Override
    public boolean addUser(Connection connection, User user) {
        PreparedStatement preparedStatement = null;
        int execute = 0;
        if(connection!=null){
            String sql = "insert into smbms_user ( userCode, userName," +
                    " userPassword, gender, birthday, phone, address, userRole, creationDate)"+
                    "values (?,?,?,?,?,?,?,?,?);";
            String userCode = user.getUserCode();
            String userName = user.getUserName();
            String userPassword = user.getUserPassword();
            Integer gender = user.getGender();
            Date birthday = user.getBirthday();
            String phone = user.getPhone();
            String address = user.getAddress();
            Integer userRole = user.getUserRole();
            Date creationDate = new Date();
            if(StringUtils.isNullOrEmpty(userCode)){
                userCode = "";
            }
            if(StringUtils.isNullOrEmpty(userName)){
                userName = "";
            }
            if(StringUtils.isNullOrEmpty(userPassword)){
                userPassword = "";
            }
            if(StringUtils.isNullOrEmpty(phone)){
                phone = "";
            }
            if(StringUtils.isNullOrEmpty(address)){
                address = "";
            }
            Object params[] = {userCode,userName,userPassword,gender,birthday,phone,address,userRole,creationDate};
            try {
                execute = BaseDao.execute(connection,sql,params,preparedStatement);
                BaseDao.close(null,null,preparedStatement);//链接不关
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(execute>0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean userExist(Connection connection, String userCode) {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        boolean flag = false;
        if(connection!=null){
            String sql = "select * from smbms_user where userCode = ?";
            Object params[] = {userCode};
            try {
                rs = BaseDao.execute(connection,sql,params,rs,preparedStatement);
                BaseDao.close(null,null,preparedStatement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if(rs.next()){
                flag = true;
            }else {
                flag = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public int deleteUserById(Connection connection, String userID) {
        PreparedStatement preparedStatement = null;
        int execute = 0;
        if(connection!=null){
            String sql = "delete from smbms_user where id = ?";
            Object params[] = {userID};
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
    public int modifyUser(Connection connection, User user) throws Exception{
        PreparedStatement preparedStatement = null;
        int execute = 0;
        if(connection!=null){
            String userName = user.getUserName();
            Integer gender = user.getGender();
            Date birthday = user.getBirthday();
            String phone = user.getPhone();
            String address = user.getAddress();
            Integer userRole = user.getUserRole();
            Integer userId = user.getId();
            Date modifyDate = new Date();
            if(StringUtils.isNullOrEmpty(userName)){
                userName = "";
            }
            if(StringUtils.isNullOrEmpty(phone)){
                phone = "";
            }
            if(StringUtils.isNullOrEmpty(address)){
                address = "";
            }
            String sql = " update smbms_user set userName = ?,gender = ?,birthday = ?," +
                    "phone = ?,address = ?,userRole = ?,modifyDate = ? where id = ?";
            Object params[] = {userName,gender,birthday,phone,address,userRole,modifyDate,userId};
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
    public User getUserById(Connection connection, String userId) throws SQLException {
        User user = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        if(null != connection){
            String sql = "select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.id=? and u.userRole = r.id";
            Object[] params = {userId};
            rs = BaseDao.execute(connection, sql,params,rs,pstm);
            if(rs.next()){
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreateBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));
                user.setUserRoleName(rs.getString("userRoleName"));
            }
            BaseDao.close(null,rs,pstm);
        }
        return user;
    }

}
