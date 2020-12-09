package com.jk.servlet.user;

import com.jk.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Administrator
 * @date 2020/12/6 17 4634
 * @description
 */
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getSession().removeAttribute(Constants.USER_SESSION);//移出session即可
        resp.sendRedirect(req.getContextPath()+"/login.jsp");
    }
}
