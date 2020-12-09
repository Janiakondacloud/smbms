package com.jk.dao.user;

import com.jk.dao.BaseDao;
import com.jk.entity.Role;
import com.jk.entity.User;
import com.mysql.jdbc.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
            String sql = "update smbms_user set userPassword = ? where userCode = ?";
            Object params[] = {userPassword,userCode};
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
    public int getUserCount(Connection connection, String userName, int userRole) {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        int count = 0;
        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_user u,smbms_role r where u.userRole = r.id");
            ArrayList<Object> lists = new ArrayList<>();//存放参数
            if (!StringUtils.isNullOrEmpty(userName)) {
                sql.append(" and u.userName like ?");
                lists.add("%" + userName + "%");//index：0
            }
            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                lists.add(userRole);//index:1
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
            sql.append(" order by creationDate DESC limit ?,?");//后面参数为页面行个数，倒数第二个为第几页
            currentPageNo = (currentPageNo-1)*pageSize;
            lists.add(currentPageNo);
            lists.add(pageSize);
            Object[] params = lists.toArray();
            System.out.println("sql---->"+sql.toString());
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
}
