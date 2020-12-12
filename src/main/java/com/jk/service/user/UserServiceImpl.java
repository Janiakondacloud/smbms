package com.jk.service.user;

import com.jk.dao.BaseDao;
import com.jk.dao.user.UserDao;
import com.jk.dao.user.UserImpl;
import com.jk.entity.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Administrator
 * @date 2020/12/6 15 3715
 * @description
 */
public class UserServiceImpl implements UserService{
    /*业务层都会调用Dao层，我们要引入Dao层*/
    private UserDao userDao;
    public UserServiceImpl(){
        this.userDao = new UserImpl();
    }
    @Override
    public User login(String userCode,String userPassword){
        Connection connection = null;
        User user = null;
        connection = BaseDao.getConnection();
        /*通过业务层来调用Dao层的代码*/
        user = userDao.getLoginUser(connection, userCode,userPassword);
        BaseDao.close(connection,null,null);

        return user;
    }

    @Override
    public boolean updatePwd(String userCode, String userPassword) {
        boolean flag = false;
        Connection connection = null;
        connection = BaseDao.getConnection();
        try {
            /*这一步设置事务*/
            connection.setAutoCommit(false);
            boolean check = userDao.updatePwd(connection, userCode,userPassword);
            connection.commit();
            if(check==true){
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }finally {
            BaseDao.close(connection,null,null);
        }
        return flag;
    }

    @Override
    public int getUserCount(String userName, int userRole) {
        Connection connection = null;
        int count = 0;
        connection = BaseDao.getConnection();
        count = userDao.getUserCount(connection, userName, userRole);
        BaseDao.close(connection,null,null);
        return count;
    }

    @Override
    public List<User> getUserList(String userName, int userRole, int currentPageNo, int pageSize) {
        Connection connection = null;
        List<User> userList = null;
        try {
            connection = BaseDao.getConnection();
            userList = userDao.getUserList(connection,userName,userRole,currentPageNo,pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.close(connection,null,null);
        }
        return userList;
    }

    @Override
    public boolean addUser(User user) {
        boolean flag = false;
        Connection connection = null;
        connection = BaseDao.getConnection();
        try {
            connection.setAutoCommit(false);
            flag = userDao.addUser(connection,user);
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
        return flag;
    }

    @Override
    public boolean userExist(String userCode) {
        boolean flag = false;
        Connection connection = null;
        connection = BaseDao.getConnection();
        /*通过业务层来调用Dao层的代码*/
        flag = userDao.userExist(connection,userCode);
        BaseDao.close(connection,null,null);
        return flag;
    }

    @Override
    public boolean deleteUserById(String userId) {
        boolean flag = false;
        Connection connection = null;
        connection = BaseDao.getConnection();
        int excute = 0;
        try {
            connection.setAutoCommit(false);
            excute = userDao.deleteUserById(connection,userId);//记得提交事务
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
    public boolean modifyUser(User user) {
        boolean flag = false;
        Connection connection = null;
        connection = BaseDao.getConnection();
        int excute = 0;
        try {
            connection.setAutoCommit(false);
            excute = userDao.modifyUser(connection,user);
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
        if(excute>0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public User getUserById(String userId) {
        Connection connection = null;
        connection = BaseDao.getConnection();
        User user = null;
        try {
            connection.setAutoCommit(false);
            user = userDao.getUserById(connection,userId);
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
        return user;
    }
}
