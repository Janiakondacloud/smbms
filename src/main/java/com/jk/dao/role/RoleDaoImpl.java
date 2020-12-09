package com.jk.dao.role;

import com.jk.dao.BaseDao;
import com.jk.entity.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @date 2020/12/7 22 4028
 * @description
 */
public class RoleDaoImpl implements RoleDao{
    @Override
    public List<Role> getRoleList(Connection connection) {
        List<Role> roleList = new ArrayList<Role>();
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        String sql = "select id,roleCode,roleName from smbms_role";
        if(connection!=null) {
            try {
                Object [] params = {};
                rs = BaseDao.execute(connection,sql,params,rs,preparedStatement);
                while (rs.next()){
                    Role role = new Role();
                    role.setId(rs.getInt("id"));
                    role.setRoleName(rs.getString("roleName"));
                    roleList.add(role);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                BaseDao.close(null,rs,preparedStatement);
            }
        }
        return roleList;
    }
}
