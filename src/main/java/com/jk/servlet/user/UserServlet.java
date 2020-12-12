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
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Administrator
 * @date 2020/12/7 14 2529
 * @description
 */
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*实现方法的解耦，复用*/
        String method = req.getParameter("method");
        if("savepwd".equals(method)&&method!=null){
            this.savePwd(req,resp);
        }else {
            if("pwdmodify".equals(method)&&method!=null){
                this.pwdModify(req,resp);
            }
            if ("query".equals(method)&&method!=null){
                this.query(req,resp);
            }
            if("add".equals(method)&&method!=null){
                this.addUser(req,resp);
            }
            if("getrolelist".equals(method)&&method!=null){
                this.getRolelist(req,resp);
            }
            if(method != null && "modify".equals(method)){
                this.getUserById(req, resp,"usermodify.jsp");
            }
            if("deluser".equals(method)&&method!=null){
                this.delUser(req,resp);
            }
            if("ucexist".equals(method)&&method!=null){
                this.userExist(req,resp);
            }
            if("modifyexe".equals(method)&&method!=null){
                this.modifyUser(req,resp);
            }
            if("view".equals(method)&&method!=null){
                this.viewUser(req,resp);
            }
        }
}
    private void getUserById(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
        /*获取前端userlist.jsp请求携带的参数，返回给usermodify.jsp参数user*/
        String id = req.getParameter("uid");
        if(!StringUtils.isNullOrEmpty(id)){
            //调用后台方法得到user对象
            UserService userService = new UserServiceImpl();
            User user = userService.getUserById(id);
            req.setAttribute("user", user);
            req.getRequestDispatcher(url).forward(req, resp);
        }
    }

    private void viewUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*获取前端userlist.jsp请求携带的参数，返回给userview.jsp参数user*/
        String userId = req.getParameter("uid");
        UserService userService = new UserServiceImpl();
        if(userId!=null){
            User user = userService.getUserById(userId);
            req.setAttribute("user",user);
            req.getRequestDispatcher("/jsp/userview.jsp").forward(req,resp);
        }else {
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
        }
    }

    private void modifyUser(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        /*获取前端usermodify.jsp请求携带的参数，并作重定向处理*/
        String userId = req.getParameter("uid");
        String userName = new String(req.getParameter("userName").getBytes("ISO-8859-1"),"UTF-8");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = new String(req.getParameter("address").getBytes("ISO-8859-1"),"UTF-8");
        String userRole = new String(req.getParameter("userRole").getBytes("ISO-8859-1"),"UTF-8");
        UserService userService = new UserServiceImpl();
        User user = new User();
        try {
            user.setId(Integer.parseInt(userId));
            user.setUserName(userName);
            user.setGender(Integer.parseInt(gender));
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
            user.setPhone(phone);
            user.setAddress(address);
            user.setUserRole(Integer.parseInt(userRole));
            user.setModifyBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
            user.setModifyDate(new Date());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(userService.modifyUser(user)){
            try {
                resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                req.getRequestDispatcher("usermodify.jsp").forward(req, resp);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void delUser(HttpServletRequest req, HttpServletResponse resp) {
        /*获取前端userlist.jsp AJAX请求携带的参数，并返回参数给前端JS显示*/
        String useId = req.getParameter("uid");
        HashMap<String,String> hashMap = new HashMap<>();
        UserService userService = new UserServiceImpl();
        if(useId!=null){
            if(userService.deleteUserById(useId)){
                hashMap.put("delResult","true");
            }else {
                hashMap.put("delResult","false");
            }
        }
        resp.setContentType("application/json");
        PrintWriter writer = null;
        try {
            writer = resp.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.write(JSONArray.toJSONString(hashMap));
        writer.flush();
        writer.close();
    }

    private void userExist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        /*获取前端useradd.jsp AJAX请求携带的参数，返回参数让前端响应*/
        String userCode = req.getParameter("userCode");
        HashMap<String,String> hashMap = new HashMap<>();
        UserService userService = new UserServiceImpl();
        if(userCode!=null){
            if(userService.userExist(userCode)){
                hashMap.put("userCode","exist");
            }
            else {
                hashMap.put("userCode","false");
            }
        }
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(hashMap));
        writer.flush();
        writer.close();
    }

    private void getRolelist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        /*获取前端useradd.jsp AJAX请求携带的参数，返回给useradd.jsp参数让前端遍历*/
        RoleService roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(roleList.toArray()));
        writer.flush();
        writer.close();
    }

    private void addUser(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        /*获取前端useradd.jsp请求携带的参数，并让前端作重定向处理*/
        UserService userService = new UserServiceImpl();
        String userCode = req.getParameter("userCode");
        String userName = new String(req.getParameter("userName").getBytes("ISO-8859-1"),"UTF-8");
        String userPassword = req.getParameter("userPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = new String(req.getParameter("address").getBytes("ISO-8859-1"),"UTF-8");
        String userRole = new String(req.getParameter("userRole").getBytes("ISO-8859-1"),"UTF-8");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedBirthday = null;
        try {
            parsedBirthday = sdf.parse(birthday);
            User user = new User();
            user.setUserCode(userCode);
            user.setUserName(userName);
            user.setUserPassword(userPassword);
            user.setGender(Integer.parseInt(gender));
            user.setBirthday(parsedBirthday);
            user.setPhone(phone);
            user.setAddress(address);
            user.setUserRole(Integer.parseInt(userRole));
            if(userService.addUser(user)){
                /*调至用户列表页面*/
                resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
            }else {
                /*相当于重新填写*/
                req.getRequestDispatcher("useradd.jsp").forward(req,resp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void query(HttpServletRequest req, HttpServletResponse resp) {
        /*获取前端userlist.jsp请求携带的参数，返回给userlist.jsp几个参数*/
        String queryname = req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");//这里第一默认是第一页
        int queryUserRole = 0;
        int currentPageNo = 1;
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
        PageSupport pageSupport = new PageSupport();
        pageSupport.setPageSize(5);
        pageSupport.setTotalCount(userCount);
        pageSupport.setTotalPageCount(userCount);
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setTotalPageCountByRs();
        totalPageCount = pageSupport.getTotalPageCount();
        if(currentPageNo<1){
            currentPageNo = 1;
       }else if(currentPageNo>pageSupport.getTotalPageCount()){
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
            req.getRequestDispatcher("userlist.jsp").forward(req,resp);//相当于刷新页面
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req,resp);
    }
    public void savePwd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*从session拿取信息*/
        Object user = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");
        String rnewpassword = req.getParameter("rnewpassword");
        if(user!=null&&!StringUtils.isNullOrEmpty(newpassword)&&!StringUtils.isNullOrEmpty((rnewpassword))){
            boolean flag = false;
            if(newpassword.equals(rnewpassword)){
                UserService userService = new UserServiceImpl();
                flag = userService.updatePwd(((User)user).getUserCode(),newpassword);
            }
            if(flag){
                /*移出当前session*/
                req.setAttribute(Constants.USER_MESSAGE,"修改密码成功，请退出使用新密码进行登陆。");
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
        /*从session拿取信息*/
        Object user = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldPassword = req.getParameter("oldpassword");

        Map<String, String> resultMap = new HashMap<>();
        if(user==null){
            /*设置session过期*/
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

