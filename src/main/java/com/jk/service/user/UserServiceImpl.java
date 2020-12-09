package com.jk.service.user;

import com.jk.dao.BaseDao;
import com.jk.dao.user.UserDao;
import com.jk.dao.user.UserImpl;
import com.jk.entity.User;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
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
        /*通过业务层来调用Dao层的代码*/
        flag = userDao.updatePwd(connection, userCode,userPassword);
        BaseDao.close(connection,null,null);
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




    @Test
    public void test(){
       UserServiceImpl service = new UserServiceImpl();
       //service.updatePwd("admin","123456");
      // User admin = service.login("admin","123456");
        int count = service.getUserCount(null, 0);
        System.out.println("查到了："+count+"行");

        //System.out.println(admin.getUserName());
    }

}
