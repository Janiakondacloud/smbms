package com.jk.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.jk.entity.Role;
import com.jk.entity.User;
import com.jk.service.role.RoleService;
import com.jk.service.role.RoleServiceImpl;
import com.jk.service.user.UserService;
import com.jk.service.user.UserServiceImpl;
import com.jk.util.Constants;
import com.jk.util.PageSupport;
import com.mysql.jdbc.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @date 2020/12/7 14 2529
 * @description
 */
public class UserServlet extends HttpServlet {



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //实现方法的解耦，复用。
        String method = req.getParameter("method");
        if(method.equals("savepwd")&&method!=null){
            this.savePwd(req,resp);
        }else {
            if(method.equals("pwdmodify")&&method!=null){
                this.pwdModify(req,resp);
            }
            if (method.equals("query")&&method!=null){
                this.query(req,resp);
            }
        }
}
// //查询用户列表,重点以及难点！！！！！！！！！！！！！！！
    public void query(HttpServletRequest req, HttpServletResponse resp) {
        //获取前端数据
        String queryname = req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");//这里第一默认是第一页
        int queryUserRole = 0;
        int currentPageNo = 1;//默认当前第一页
        int totalPageCount;
        if(!StringUtils.isNullOrEmpty(temp)){
            queryUserRole = Integer.parseInt(temp);
        }
        if(StringUtils.isNullOrEmpty(queryname)){
            queryname = "";
        }
        if(!StringUtils.isNullOrEmpty(pageIndex)){
            currentPageNo = Integer.parseInt(pageIndex);
        }
        UserService userService = new UserServiceImpl();
        RoleService roleService = new RoleServiceImpl();
        int userCount = userService.getUserCount(queryname, queryUserRole);

        //第一次走这个页面一定是第一页并且大小固定5
        PageSupport pageSupport = new PageSupport();
        pageSupport.setPageSize(5);
        pageSupport.setTotalCount(userCount);
        pageSupport.setTotalPageCount(userCount);
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setTotalPageCountByRs();//这一步是完成分页的关键
        totalPageCount = pageSupport.getTotalPageCount();
        //设置尾页和首页
        if(currentPageNo<1){
            currentPageNo = 1;
       }else if(currentPageNo>pageSupport.getTotalPageCount()){//如果当前页面大于最大页面
            currentPageNo = pageSupport.getTotalPageCount();
        }

        List<User> userList = userService.getUserList(queryname,queryUserRole,currentPageNo,5);
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("userList",userList);
        req.setAttribute("roleList",roleList);
        req.setAttribute("totalCount",userCount);
        req.setAttribute("currentPageNo",currentPageNo);
        req.setAttribute("totalPageCount",totalPageCount);
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //实现方法的解耦，复用。
        this.doGet(req,resp);
    }
    public void savePwd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Session中读取id信息
        Object user = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");
        String rnewpassword = req.getParameter("rnewpassword");
        System.out.println(user);
        System.out.println(newpassword+":"+rnewpassword);
        if(user!=null&&!StringUtils.isNullOrEmpty(newpassword)&&!StringUtils.isNullOrEmpty((rnewpassword))){
            boolean flag = false;
            if(newpassword.equals(rnewpassword)){
                UserService userService = new UserServiceImpl();
                System.out.println(((User)user).getUserName());
                flag = userService.updatePwd(((User)user).getUserCode(),newpassword);
                System.out.println(flag);
            }
            if(flag){
                req.setAttribute(Constants.USER_MESSAGE,"修改密码成功，请退出使用新密码进行登陆。");
                //移出当前session
                req.getSession().removeAttribute(Constants.USER_SESSION);
            }else {
                req.setAttribute(Constants.USER_MESSAGE,"修改密码失败");
            }
        }else {
            req.setAttribute(Constants.USER_MESSAGE,"请重新修改密码");
        }
        req.getRequestDispatcher("pwdmodify.jsp").forward(req,resp);
    }
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Session中读取id信息
        Object user = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldPassword = req.getParameter("oldpassword");
        //万能的Map
        Map<String, String> resultMap = new HashMap<>();
        if(user==null){
            //Session过期
            resultMap.put("result","sessionerror");
        }else {
            if(StringUtils.isNullOrEmpty(oldPassword)){
                resultMap.put("result","error");
            }else {
                    String userPassword = ((User)user).getUserPassword();
                    if(oldPassword.equals(userPassword)){
                        resultMap.put("result","true");
                    }else {
                        resultMap.put("result","false");
                    }
                }
            }
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }
    }

