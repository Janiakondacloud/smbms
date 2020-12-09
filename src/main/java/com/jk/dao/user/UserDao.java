package com.jk.dao.user;

import com.jk.entity.Role;
import com.jk.entity.User;

import java.sql.Connection;
import java.util.List;

/**
 * @author Administrator
 * @date 2020/12/6 15 1404
 * @description
 */
public interface UserDao {
    //得到要登陆的用户
    public User getLoginUser(Connection connection, String userCode,String userPassword);
    //修改当前用户密码
    public boolean updatePwd(Connection connection, String userCode,String userPassword);
    //根据用户名或者用户角色类型（经理/管理员）查询用户总数
    public int getUserCount(Connection connection,String userName,int roleCode);
    //获取用户列表
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws Exception;

}
