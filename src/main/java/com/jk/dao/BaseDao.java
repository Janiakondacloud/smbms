package com.jk.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @author Administrator
 * @date 2020/12/4 16 5105
 * @description
 */
public class BaseDao {
    private static String url;
    private static String driver;
    private static String password;
    private static String username;
    //类加载的时候静态代码块就加载了
    static {
        Properties properties = new Properties();
        InputStream in = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        password = properties.getProperty("password");
        username = properties.getProperty("username");
    }
    //获得数据库链接
    public static Connection getConnection(){
            Connection connection = null;
        try {
            Class.forName(driver);
             connection = DriverManager.getConnection(url,username,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
    //查询数据库操作
    public static ResultSet execute(Connection connection,String sql, Object[] params, ResultSet resultSet, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);
        System.out.println(sql+"----------------resultSet");
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1,params[i]);
        }
        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }
    //增删改数据库操作
    public static int execute(Connection connection,String sql,Object[] params,PreparedStatement preparedStatement) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);
        System.out.println(sql+"----------------int");
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1,params[i]);
        }
        int count = 0;
        return preparedStatement.executeUpdate();
    }
    public static boolean close(Connection connection,ResultSet resultSet,PreparedStatement preparedStatement){
        boolean flag = true;
        try {
            if(resultSet!=null){
                resultSet.close();
                resultSet = null;//这一步是让gc回收
            }
        } catch (SQLException e) {
            e.printStackTrace();
            flag = false;
        }
        try {
            if(preparedStatement!=null){
                preparedStatement.close();
                preparedStatement = null;//这一步是让gc回收
            }
        } catch (SQLException e) {
            e.printStackTrace();
            flag = false;
        }
        try {
            if(connection!=null){
                connection.close();
                connection = null;//这一步是让gc回收
            }
        } catch (SQLException e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }
}
