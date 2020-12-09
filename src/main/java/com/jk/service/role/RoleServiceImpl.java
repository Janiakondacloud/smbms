package com.jk.service.role;

import com.jk.dao.BaseDao;
import com.jk.dao.role.RoleDao;
import com.jk.dao.role.RoleDaoImpl;
import com.jk.entity.Role;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Administrator
 * @date 2020/12/7 22 4255
 * @description
 */
public class RoleServiceImpl implements RoleService{
    /*很重要的一步*/
    private RoleDao roleDao;
    public RoleServiceImpl(){
        this.roleDao = new RoleDaoImpl();
    }
    @Override
    public List<Role> getRoleList() {
        Connection connection = null;
        List<Role> roleList = null;
        connection = BaseDao.getConnection();
        roleList = roleDao.getRoleList(connection);
        BaseDao.close(connection,null,null);
        return roleList;
    }
    @Test
    public void test(){
        RoleService roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        Iterator<Role> it = roleList.iterator();
        while(it.hasNext()){
            System.out.println(it.next().getRoleName());
        }
    }
}
