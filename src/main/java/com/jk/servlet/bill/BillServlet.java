package com.jk.servlet.bill;
import com.alibaba.fastjson.JSONArray;
import com.jk.entity.Bill;
import com.jk.entity.Provider;
import com.jk.entity.User;
import com.jk.service.bill.BillService;
import com.jk.service.bill.BillServiceImpl;
import com.jk.service.provider.ProviderService;
import com.jk.service.provider.ProviderServiceImpl;
import com.jk.util.Constants;
import com.jk.util.PageSupport;
import com.mysql.jdbc.StringUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 * @date 2020/12/10 21 3411
 * @description
 */
public class BillServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req,resp);
    }
/***
 * @函数功能：前端参数method对应的方法携带的参数来访问具体的方法
 * @param: req 请求
 * @param: resp 响应
 */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if(method!=null&&"query".equals(method)){
            this.query(req,resp);
        }
        if(method!=null&&"delbill".equals(method)){
            this.delBill(req,resp);
        }
        if(method!=null&& "add".equals(method)){
            this.addBill(req,resp);
        }
        if(method!=null&& "getproviderlist".equals(method)){
            this.getProviderList(req,resp);
        }
        if(method!=null&& "view".equals(method)){
            this.viewBill(req,resp);
        }
        if(method!=null&& "modify".equals(method)){
            this.getBillById(req,resp,"billmodify.jsp");
        }
        if("billexist".equals(method)&&method!=null){
            this.billExist(req,resp);
        }
        if("modifysave".equals(method)&&method!=null){
            this.modifyBill(req,resp);
        }
    }

    private void billExist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        /*获取前端billadd.jsp请求携带的参数用于Ajax判断是否可以添加该Bill*/
        String billCode = req.getParameter("billCode");
        /*Map来存储返回给前端参数的key-value*/
        HashMap<String,String> hashMap = new HashMap<>();
        BillService billService = new BillServiceImpl();
        if(!StringUtils.isNullOrEmpty(billCode)){
            if(billService.billExist(billCode)){
                hashMap.put("billCode","exist");
            }
            else {
                hashMap.put("billCode","ok");
            }
        }
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(hashMap));
        writer.flush();
        writer.close();
    }

    private void getBillById(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
        /*获取前端billlist.jsp请求携带的参数，返回给billmodify.jsp参数bill*/
        String id = req.getParameter("billid");
        if(!StringUtils.isNullOrEmpty(id)){
            /*获取前端billlist.jsp请求携带的参数，返回给billmodify.jsp参数bill*/
            BillService billService = new BillServiceImpl();
            Bill bill = billService.getBillById(Integer.parseInt(id));
            req.setAttribute("bill",bill);
            req.getRequestDispatcher(url).forward(req, resp);
        }
    }

    private void modifyBill(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        /*获取前端billmodify.jsp请求携带的参数，再做返回判断，成功则返回列表页面，不成功就刷新*/
       /*这里如果不用这种方式创建则会出现乱码.......*/
        String billCode = new String(req.getParameter("billCode").getBytes("ISO-8859-1"),"UTF-8");
        String productName =new String(req.getParameter("productName").getBytes("ISO-8859-1"),"UTF-8");
        String productUnit =new String(req.getParameter("productUnit").getBytes("ISO-8859-1"),"UTF-8");
        String productCount =req.getParameter("productCount");
        String totalPrice = req.getParameter("totalPrice");
        String providerId = req.getParameter("providerId");
        String isPayment =req.getParameter("isPayment");
        String id =req.getParameter("billid");
        BillService billService = new BillServiceImpl();
        Bill bill = new Bill();
        try {
            bill.setId(Integer.parseInt(id));
            bill.setBillCode(billCode);
            bill.setProductName(productName);
            bill.setProductUnit(productUnit);
            bill.setProductCount(new BigDecimal(productCount));
            bill.setTotalPrice(new BigDecimal(totalPrice));
            bill.setProviderId(Integer.valueOf(providerId));
            bill.setIsPayment(Integer.valueOf(isPayment));
            /*设置该订单被修改的时间和修改者的角色类型*/
            bill.setModifyDate(new Date());
            bill.setModifyBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getUserRole());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(billService.modifyBill(bill)){
            try {
                /*成功转发给订单列表页面*/
                resp.sendRedirect(req.getContextPath()+"/jsp/bill.do?method=query");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                /*失败就刷行该修改页面*/
                req.getRequestDispatcher("billmodify.jsp").forward(req, resp);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void viewBill(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        /*获取前端billlist.jsp请求携带的参数，返回给billmodify.jsp参数bill*/
        String billId = req.getParameter("billid");
        BillService billService = new BillServiceImpl();
        if(billId!=null){
            Bill bill = billService.getBillById(Integer.parseInt(billId));
            req.setAttribute("bill",bill);
            req.getRequestDispatcher("/jsp/billview.jsp").forward(req,resp);
        }else {
            resp.sendRedirect(req.getContextPath()+"/jsp/bill.do?method=query");
        }
    }

    private void getProviderList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        /*这里好像是多余，但是不这样设置前端会乱码......*/
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        ProviderService providerService = new ProviderServiceImpl();
        List<Provider> providerList = providerService.getAllProIdList();
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(providerList.toArray()));
        writer.flush();
        writer.close();
    }

    private void addBill(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        /*获取前端billadd.jsp请求携带的参数，再重定向*/
        String billCode = new String(req.getParameter("billCode").getBytes("ISO-8859-1"),"UTF-8");
        String productName = new String(req.getParameter("productName").getBytes("ISO-8859-1"),"UTF-8");
        String productUnit = new String(req.getParameter("productUnit").getBytes("ISO-8859-1"),"UTF-8");
        String productCount = new String(req.getParameter("productCount").getBytes("ISO-8859-1"),"UTF-8");
        String totalPrice = new String(req.getParameter("totalPrice").getBytes("ISO-8859-1"),"UTF-8");
        String providerId = new String(req.getParameter("providerId").getBytes("ISO-8859-1"),"UTF-8");
        String isPayment = new String(req.getParameter("isPayment").getBytes("ISO-8859-1"),"UTF-8");
        BillService billService = new BillServiceImpl();
        try {
            Bill bill = new Bill();
            bill.setBillCode(billCode);
            bill.setProductName(productName);
            bill.setProductUnit(productUnit);
            bill.setProductCount(new BigDecimal(productCount));
            bill.setTotalPrice(new BigDecimal(totalPrice));
            bill.setProviderId(Integer.valueOf(providerId));
            bill.setIsPayment(Integer.valueOf(isPayment));
            /*设置该订单被创建的时间和创建者的角色类型*/
            bill.setCreationDate(new Date());
            bill.setCreateBy(((User)req.getSession().getAttribute(Constants.USER_SESSION)).getUserRole());
            if(billService.addBill(bill)){
                resp.sendRedirect(req.getContextPath()+"/jsp/bill.do?method=query");
            }else {
                req.getRequestDispatcher("billadd.jsp").forward(req,resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void query(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
        /*获取前端billlist.jsp请求携带的参数，返回给billlist.jsp几个参数*/
        String queryProductName = req.getParameter("queryProductName");
        String tempProviderId = req.getParameter("queryProviderId");
        String pageIndex = req.getParameter("pageIndex");
        String tempIsPayment = req.getParameter("queryIsPayment");
        int currentPageNo = 1;
        int totalPageCount;
        int providerId = 0;
        int isPayment = 0;
        if(StringUtils.isNullOrEmpty(queryProductName)){
            queryProductName = "";
        }
        if(!StringUtils.isNullOrEmpty(tempProviderId)){
            providerId = Integer.valueOf(tempProviderId);
        }
        if(!StringUtils.isNullOrEmpty(tempIsPayment)){
            isPayment = Integer.valueOf(tempIsPayment);
        }
        if(!StringUtils.isNullOrEmpty(pageIndex)){
            currentPageNo = Integer.valueOf(pageIndex);
        }
        BillService billService = new BillServiceImpl();
        ProviderService providerService = new ProviderServiceImpl();
        int billCount = billService.getBillCount(queryProductName,providerId,isPayment);
        PageSupport pageSupport = new PageSupport();
        pageSupport.setPageSize(5);
        pageSupport.setTotalCount(billCount);
        pageSupport.setTotalPageCount(billCount);
        pageSupport.setCurrentPageNo(currentPageNo);
        /*这一步是完成分页的关键*/
        pageSupport.setTotalPageCountByRs();
        totalPageCount = pageSupport.getTotalPageCount();
        /*currentPageNo在变，设置首尾页*/
        if(currentPageNo<1){
            currentPageNo = 1;
        }else if(currentPageNo>pageSupport.getTotalPageCount()){
            /*如果当前页面大于最大页面*/
            currentPageNo = pageSupport.getTotalPageCount();
        }
        List<Provider> providers = providerService.getAllProIdList();
        List<Bill> billList = billService.getBillList(queryProductName,providerId,isPayment,currentPageNo,5);
        /*返回给前端billlist.jsp的参数*/
        req.setAttribute("providerList",providers);
        req.setAttribute("billList",billList);
        req.setAttribute("totalCount",billCount);
        req.setAttribute("currentPageNo",currentPageNo);
        req.setAttribute("totalPageCount",totalPageCount);
        try {
            /*刷新该页面*/
            req.getRequestDispatcher("billlist.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void delBill(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        /*获取前端billlist.jsp请求携带的参数，Ajax异步处理请求重定向*/
        String billid = new String(req.getParameter("billid").getBytes("ISO-8859-1"),"UTF-8");
        HashMap<String,String> hashMap = new HashMap<String,String>();
        BillService billService = new BillServiceImpl();
        if(!StringUtils.isNullOrEmpty(billid)){
            if(billService.deleteBillById(billid)){
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
        resp.sendRedirect(req.getContextPath()+"/jsp/bill.do?method=query");
        }
    }

