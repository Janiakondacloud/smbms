package com.jk.filter;
import com.jk.util.Constants;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Administrator
 * @date 2020/12/6 17 5940
 * @description 权限过滤器 让没有登陆的用户只能访问error.jsp页面
 */
public class SysFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("SysFilter初始化成功");
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
        System.out.println("SysFilter销毁成功");
    }
}
