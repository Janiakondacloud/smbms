package com.jk.dao.provider;

import com.jk.entity.Provider;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
/**
 * @author Administrator
 * @date 2020/12/9 16 0815
 * @description 供货商接口类
 */
public interface ProviderDao {
    /***
     * @函数功能：利用模糊查询根据供应商编号或者名字找到对应的供应商总数
     * @param: connection Service层调用时传入
     * @param: proCode 供货商编码
     * @param: proName 供货商名字
     * @return：返回供应商总数
     */
    public int getProviderCount(Connection connection,String proCode,String proName);
    /*** 
     * @函数功能：
     * @param: connection Service层调用时传入
     * @param: proCode 供货商编码
     * @param: proName 供货商名字
     * @param: currentPageNo 当前页码
     * @param: pageSize 每一个大小
     * @return：
     */
    public List<Provider> getProviderList(Connection connection,String proCode,String proName,int currentPageNo, int pageSize) throws Exception;
   /***
    * @函数功能：获得全部的供供货商名字
    * @param: connection
    * @return：返回全部List
    */
    public List<Provider> getAllProviderIdList(Connection connection) throws Exception;
    /***
     * @函数功能：
     * @param: connection Service层调用时传入
     * @param: provider 要添加的Provider
     * @return：所有的ProviderIdList
     */
    public int addProvider(Connection connection,Provider provider);
    /***
     * @函数功能：判断Provider是否存在
     * @param: connection Service层调用时传入
     * @param: proCode 供应商编码
     * @return：大于0存在 等于0不存在
     */
    public int providerExist(Connection connection,String proCode);
    /*** 
     * @函数功能：删除供应商
     * @param: connection Service层调用时传入
     * @param: providerId 供应商id
     * @return：1成功 0失败
     */
    public int deleteProviderById(Connection connection,String providerId);
    /***
     * @函数功能：修改供应商
     * @param: connection Service层调用时传入
     * @param: Provider 要修改的Provider
     * @return：1成功 0失败
     */
    public int modifyProvider(Connection connection, Provider Provider) throws Exception;
    /***
     * @函数功能：获取供应商
     * @param: connection Service层调用时传入
     * @param: ProviderId 要修改的ProviderId
     * @return：1成功 0失败
     */
    public Provider getProviderById(Connection connection, Integer providerId) throws SQLException;
}
