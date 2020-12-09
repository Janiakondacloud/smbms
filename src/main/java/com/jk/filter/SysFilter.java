package com.jk.filter;

import com.jk.util.Constants;
import com.sun.deploy.net.HttpResponse;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author Administrator
 * @date 2020/12/6 17 5940
 * @description
 */
public class SysFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        if(request.getSession().getAttribute(Constants.USER_SESSION)==null){
            response.sendRedirect((request.getContextPath()+"/error.jsp"));
        }else {
            chain.doFilter(req,resp);
        }
    }

    @Override
    public void destroy() {

    }
}
