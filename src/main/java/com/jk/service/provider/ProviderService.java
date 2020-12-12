package com.jk.service.provider;
import com.jk.entity.Provider;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 * @date 2020/12/9 23 4342
 * @description 处理事务
 */
public interface ProviderService {
    //根据供应商的名字或者编号来查找供应商信息
    public List<Provider> getProviderList(String proCode, String proName, int currentPageNo, int pageSize);
    //获取所有的供应商id
    public List<Provider> getAllProIdList();
    //返回供应商总数
    public int getProviderCount(String proCode, String proName);
    //增加供应商
    public boolean addProvider(Provider provider);
    //通过供应商编号判断供应商是否存在
    public boolean providerExist(String proCode);
    //通过id删除供应商
    public HashMap<String,Integer> deleteProviderById(String providerId);
    //通过传入对象修改供应商
    public boolean modifyProvider(Provider provider);
    //通过id获取供应商
    public Provider getProviderById(Integer id);
}
