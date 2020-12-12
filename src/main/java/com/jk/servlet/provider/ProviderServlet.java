package com.jk.servlet.provider;

import com.alibaba.fastjson.JSONArray;
import com.jk.entity.Provider;
import com.jk.entity.Role;
import com.jk.entity.User;
import com.jk.service.provider.ProviderService;
import com.jk.service.provider.ProviderServiceImpl;
import com.jk.service.role.RoleService;
import com.jk.service.role.RoleServiceImpl;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 * @date 2020/12/9 21 5848
 * @description
 */
public class ProviderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if("query".equals(method)&&method!=null){
            this.query(req,resp);
        }
        if(method!=null&& "delprovider".equals(method)){
            this.delProvider(req,resp);
        }
        if(method!=null&& "add".equals(method)){
            this.addProvider(req,resp);
        }
        if(method!=null&& "getrolelist".equals(method)){
            this.getRoleList(req,resp);
        }
        if(method!=null&& "view".equals(method)){
            this.viewProvider(req,resp);
        }
        if(method!=null&& "modify".equals(method)){
            this.getProviderById(req,resp,"providermodify.jsp");
        }
        if("providerexist".equals(method)&&method!=null){
            this.providerExist(req,resp);
        }
        if("modifysave".equals(method)&&method!=null){
            this.modifyProvider(req,resp);
        }
    }

    private void viewProvider(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*获取前端providerlist.jsp请求携带的参数，返回给providerview.jsp参数provider*/
        String proId = req.getParameter("proid");
        ProviderService providerService = new ProviderServiceImpl();
        if(proId!=null){
            Provider provider = providerService.getProviderById(Integer.parseInt(proId));
            req.setAttribute("provider",provider);
            req.getRequestDispatcher("/jsp/providerview.jsp").forward(req,resp);
        }else {
            resp.sendRedirect(req.getContextPath()+"/jsp/provider.do?method=query");
        }
    }
    private void getProviderById(HttpServletRequest req, HttpServletResponse resp,String url) throws ServletException, IOException {
        /*获取前端providerlist.jsp请求携带的参数，返回给providermodify.jsp参数provider*/
        String proId = req.getParameter("proid");
        if(!StringUtils.isNullOrEmpty(proId)){
           ProviderService providerService = new ProviderServiceImpl();
           Provider provider = providerService.getProviderById(Integer.parseInt(proId));
           req.setAttribute("provider",provider);
           req.getRequestDispatcher(url).forward(req, resp);
        }
    }
    private void providerExist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        /*获取前端providerlist.jsp AJAX请求携带的参数，返回给providerlistjsp参数proCode*/
        String proCode = req.getParameter("proCode");
        HashMap<String,String> hashMap = new HashMap<>();
        ProviderService providerService = new ProviderServiceImpl();
        if(!StringUtils.isNullOrEmpty(proCode)){
            if(providerService.providerExist(proCode)){
                hashMap.put("proCode","exist");
            }
            else {
                hashMap.put("proCode","ok");
            }
        }
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(hashMap));
        writer.flush();
        writer.close();
    }
    private void modifyProvider(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        /*获取前端providerlist.jsp请求携带的参数，返回给providermodify.jsp参数provider*/
        String proCode = new String(req.getParameter("proCode").getBytes("ISO-8859-1"),"UTF-8");
        String proName =new String(req.getParameter("proName").getBytes("ISO-8859-1"),"UTF-8");
        String proContact =new String(req.getParameter("proContact").getBytes("ISO-8859-1"),"UTF-8");
        String proPhone =new String(req.getParameter("proPhone").getBytes("ISO-8859-1"),"UTF-8");
        String proAddress =new String(req.getParameter("proAddress").getBytes("ISO-8859-1"),"UTF-8");
        String proFax =new String(req.getParameter("proFax").getBytes("ISO-8859-1"),"UTF-8");
        String proDesc =new String(req.getParameter("proDesc").getBytes("ISO-8859-1"),"UTF-8");
        String id =req.getParameter("providerid");
        ProviderService providerService = new ProviderServiceImpl();
        Provider provider = new Provider();
        try {
            provider.setId(Integer.parseInt(id));
            provider.setProCode(proCode);
            provider.setProName(proName);
            provider.setProContact(proContact);
            provider.setProPhone(proPhone);
            provider.setProAddress(proAddress);
            provider.setProFax(proFax);
            provider.setProDesc(proDesc);
            provider.setModifyDate(new Date());
            provider.setModifyBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(providerService.modifyProvider(provider)){
            try {
                resp.sendRedirect(req.getContextPath()+"/jsp/provider.do?method=query");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                req.getRequestDispatcher("providermodify.jsp").forward(req, resp);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void getRoleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        /*在添加供货商的时候获取角色列表*/
        RoleService roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(roleList.toArray()));
        writer.flush();
        writer.close();
    }
    private void addProvider(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        /*获取前端provideradd.jsp请求携带的参数，再分别重定向*/
        String proCode = new String(req.getParameter("proCode").getBytes("ISO-8859-1"),"UTF-8");
        String proName = new String(req.getParameter("proName").getBytes("ISO-8859-1"),"UTF-8");
        String proContact = new String(req.getParameter("proContact").getBytes("ISO-8859-1"),"UTF-8");
        String proPhone = new String(req.getParameter("proPhone").getBytes("ISO-8859-1"),"UTF-8");
        String proAddress = new String(req.getParameter("proAddress").getBytes("ISO-8859-1"),"UTF-8");
        String proFax = new String(req.getParameter("proFax").getBytes("ISO-8859-1"),"UTF-8");
        String proDesc = new String(req.getParameter("proDesc").getBytes("ISO-8859-1"),"UTF-8");
        ProviderService providerService = new ProviderServiceImpl();
        try {
            Provider provider = new Provider();
            provider.setProCode(proCode);
            provider.setProName(proName);
            provider.setProContact(proContact);
            provider.setProPhone(proPhone);
            provider.setProAddress(proAddress);
            provider.setProFax(proFax);
            provider.setProDesc(proDesc);
            provider.setCreationDate(new Date());
            provider.setCreateBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getId());
            if(providerService.addProvider(provider)){
                resp.sendRedirect(req.getContextPath()+"/jsp/provider.do?method=query");
            }else {
                req.getRequestDispatcher("provideradd.jsp").forward(req,resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void delProvider(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        /*获取前端providerlist.jsp AJAX请求携带的参数，返回参数delResult，billCount*/
        String proId = new String(req.getParameter("proid").getBytes("ISO-8859-1"),"UTF-8");
        HashMap<String,String> hashMap = new HashMap<String,String>();
        ProviderService providerService = new ProviderServiceImpl();
        if(!StringUtils.isNullOrEmpty(proId)){
            HashMap<String, Integer> resultMap = providerService.deleteProviderById(proId);
            if(resultMap.get("result")>0){
                hashMap.put("delResult","true");
            }else if(resultMap.get("result")==0){
                hashMap.put("delResult","notexist");
              }else{
                Integer billCount =  resultMap.get("billCount");
                hashMap.put("delResult",billCount.toString());
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
    private void query(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        /*获取前端providerlist.jsp请求携带的参数，返回几个参数*/
        String queryProCode = req.getParameter("queryProCode");
        String queryProName = req.getParameter("queryProName");
        String pageIndex = req.getParameter("pageIndex");
        int currentPageNo = 1;
        int totalPageCount;
        if(StringUtils.isNullOrEmpty(queryProCode)){
            queryProCode = "";
        }
        if(StringUtils.isNullOrEmpty(queryProName)){
            queryProName = "";
        }
        if(!StringUtils.isNullOrEmpty(pageIndex)){
            currentPageNo = Integer.parseInt(pageIndex);
        }
        ProviderService providerService = new ProviderServiceImpl();
        int providerCount = providerService.getProviderCount(queryProCode,queryProName);
        PageSupport pageSupport = new PageSupport();
        pageSupport.setPageSize(5);
        pageSupport.setTotalCount(providerCount);
        pageSupport.setTotalPageCount(providerCount);
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setTotalPageCountByRs();
        totalPageCount = pageSupport.getTotalPageCount();
        if(currentPageNo<1){
            currentPageNo = 1;
        }else if(currentPageNo>pageSupport.getTotalPageCount()){
            currentPageNo = pageSupport.getTotalPageCount();
        }
        List<Provider> providers = providerService.getProviderList(queryProCode,queryProName,currentPageNo,5);
        req.setAttribute("providerList",providers);
        req.setAttribute("totalCount",providerCount);
        req.setAttribute("currentPageNo",currentPageNo);
        req.setAttribute("totalPageCount",totalPageCount);
        try {
            req.getRequestDispatcher("providerlist.jsp").forward(req,resp);//相当于刷新页面
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


