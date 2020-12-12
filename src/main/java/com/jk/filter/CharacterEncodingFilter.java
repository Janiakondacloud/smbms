package com.jk.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author Administrator
 * @date 2020/12/4 16 5212
 * @description 编码过滤器
 */
public class CharacterEncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("CharacterEncodingFilter初始化成功");
    }
/***
 * @函数功能：把请求和响应的编码都设置为UTF-8
 * @param: request 请求
 * @param: response 响应
 * @param: chain 执行链
 * @return：
 */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        /*让其继续被其他过滤器处理，如果不doFilter()则过滤到这里就过滤不下去了*/
        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {
        System.out.println("CharacterEncodingFilter销毁成功");
    }
}
