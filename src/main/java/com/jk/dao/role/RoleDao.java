package com.jk.dao.role;

import com.jk.entity.Role;

import java.sql.Connection;
import java.util.List;

/**
 * @author Administrator
 * @date 2020/12/7 22 3938
 * @description
 */
public interface RoleDao {
    //获取角色列表-只是名字
    public List<Role> getRoleList(Connection connection);
}
