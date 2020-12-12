package com.jk.servlet.user;

import com.jk.entity.User;
import com.jk.service.user.UserService;
import com.jk.service.user.UserServiceImpl;
import com.jk.util.Constants;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.jk.util.Constants.USER_ERROR;

/**
 * @author Administrator
 * @date 2020/12/6 16 1852
 * @description
 */
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //从前端提取数据
        String userPassword = req.getParameter("userPassword");
        String userCode = req.getParameter("userCode");
        //和数据库中对比，调用服务层来进行操作。
        UserService userService = new UserServiceImpl();
        User user = userService.login(userCode,userPassword);
        if(user!=null){
            //将用户信息写入session
            req.getSession().setAttribute(Constants.USER_SESSION,user);
            resp.sendRedirect("jsp/frame.jsp");//跳转至这个页面,跳转至相对于当前请求的路径的相对页面jsp/frame.jsp
        }else {
            //转发回登陆页面和提示前端显示错误。
            req.setAttribute(Constants.USER_ERROR,"用户名或者密码输入错误");
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req,resp);
    }
}
