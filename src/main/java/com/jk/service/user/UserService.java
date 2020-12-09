package com.jk.service.user;

import com.jk.dao.user.UserDao;
import com.jk.dao.user.UserImpl;
import com.jk.entity.Role;
import com.jk.entity.User;

import java.sql.Connection;
import java.util.List;

/**
 * @author Administrator
 * @date 2020/12/6 15 3406
 * @description
 */
public interface UserService {
    public User login(String userCode,String userPassword);
    public boolean updatePwd(String userCode,String userPassword);
    public int getUserCount(String userName,int userRole);
    public List<User> getUserList(String userName, int userRole, int currentPageNo, int pageSize);


}
