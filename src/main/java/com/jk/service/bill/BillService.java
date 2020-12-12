package com.jk.service.bill;

import com.jk.entity.Bill;

import java.util.List;

/**
 * @author Administrator
 * @date 2020/12/10 19 0015
 * @description 负责增删改查事务管理和请求Dao层SQL操作
 */
public interface BillService {
    //根据供应商的名字或者编号来查找供应商信息
    public List<Bill> getBillList(String productName,Integer providerId, Integer isPayment,int currentPageNo, int pageSize);
    //返回供应商总数
    public int getBillCount(String productName, Integer providerId, Integer isPayment);
    //增加供应商
    public boolean addBill(Bill bill);
    //通过供应商编号判断供应商是否存在
    public boolean billExist(String billCode);
    //通过id删除供应商
    public boolean deleteBillById(String billId);
    //这里主要是判断该供应商下是否有订单，如果有则无法删除该供应商。
    public int deleteBillByProviderId(Integer providerId);
    //通过传入对象修改供应商
    public boolean modifyBill(Bill bill);
    //通过id获取供应商
    public Bill getBillById(Integer billId);
}
