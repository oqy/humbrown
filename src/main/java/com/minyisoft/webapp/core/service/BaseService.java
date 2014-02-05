package com.minyisoft.webapp.core.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;

/**
 * @author qingyong_ou
 * 业务处理接口基类
 * @param <T>
 * @param <C>
 */
public interface BaseService<T extends IModelObject, C extends BaseCriteria> {
	/**
	 * 新增记录
	 * @param info
	 */
	public void addNew(T info);

	/**
	 * 根据id删除记录
	 * @param id
	 * @return
	 */
	public void delete(T info);

	/**
	 * 更新（保存）记录
	 * @param info
	 * @return
	 */
	public void save(T info);

	/**
	 * 根据id查找数据库记录并转换为对应对象
	 * @param id
	 * @return
	 */
	public T getValue(String id);

	/**
	 * 根据id集合批量删除记录
	 * @param ids
	 */
	public void batchDelete(String[] ids);
	
	/**
	 * 提交记录，若记录id为空，则执行新增操作，否则执行更新操作
	 * @param info
	 */
	public void submit(T info);
	
	/**
	 * 根据过滤条件查找，返回满足条件的第一条记录
	 * @param baseCriteria
	 * @return
	 */
	public T find(C criteria);
	
	/**
	 * 返回所有记录
	 * @return
	 */
	public List<T> getCollection();
	
	/**
	 * 根据过滤条件查找，返回满足条件的所有记录
	 * @param baseCriteria
	 * @return
	 */
	public List<T> getCollection(C criteria);
	
	/**
	 * 获取记录总数
	 * @return
	 */
	public int countAll();
	
	/**
	 * 获取满足过滤条件的记录总数
	 * @param baseCriteria
	 * @return
	 */
	public int count(C criteria);
	
	// 缓存IModelObject类对应IBaseService接口
	public ConcurrentMap<Class<? extends IModelObject>,Class<? extends BaseService<? extends IModelObject, ? extends BaseCriteria>>> MODEL_SERVICE_CACHE = new ConcurrentHashMap<Class<? extends IModelObject>,Class<? extends BaseService<? extends IModelObject, ? extends BaseCriteria>>>();
}
