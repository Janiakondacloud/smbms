package com.jk.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @author Administrator
 * @date 2020/12/4 16 5105
 * @description 基础工具类
 */
public class BaseDao {
     /**
      *
     * 基础数据库连接信息
     * */
    private static String url;
    private static String driver;
    private static String password;
    private static String username;
    /*
      类加载的时候静态代码块就加载了，调用静态方法直接使用数据库信息。
    */
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
    /***
     * @函数功能：获取数据库连接
     * @return：返回配置文件中连接的数据库
     */
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
    /***
     * @函数功能：查询操作
     * @param: connection 要查询的连接
     * @param: sql 要执行的SQL语句
     * @param: params 要执行的SQL中的参数
     * @param: resultSet 结果集在这里传入方便统一关闭
     * @param: preparedStatement 安全声明
     * @return：查询的记录集
     */
    public static ResultSet execute(Connection connection,String sql, Object[] params, ResultSet resultSet, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1,params[i]);
        }
        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }
    /***
     * @函数功能：返回SQL执行后受影响的行数
     * @param: connection 要查询的连接
     * @param: sql 要执行的SQL语句
     * @param: params 要执行的SQL中的参数
     * @param: preparedStatement 安全声明
     * @return：返回SQL执行后受影响的行数
     */
    public static int execute(Connection connection,String sql,Object[] params,PreparedStatement preparedStatement) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1,params[i]);
        }
        return preparedStatement.executeUpdate();
    }
    public static boolean close(Connection connection,ResultSet resultSet,PreparedStatement preparedStatement){
        boolean flag = true;
        try {
            if(resultSet!=null){
                resultSet.close();
                /*这一步是让gc回收*/
                resultSet = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            flag = false;
        }
        try {
            if(preparedStatement!=null){
                preparedStatement.close();
                /*这一步是让gc回收*/
                preparedStatement = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            flag = false;
        }
        try {
            if(connection!=null){
                connection.close();
                /*这一步是让gc回收*/
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }
}
