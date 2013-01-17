package com.tex100.webapp.core.service;

import java.util.List;

import com.tex100.webapp.core.model.CoreBaseInfo;
import com.tex100.webapp.core.model.criteria.BaseCriteria;

public interface IBaseService<T extends CoreBaseInfo, C extends BaseCriteria> {
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
	public int delete(String id);

	/**
	 * 更新（保存）记录
	 * @param info
	 * @return
	 */
	public int save(T info);

	/**
	 * 根据id查找数据库记录并转换为对应对象
	 * @param id
	 * @return
	 */
	public T getValue(String id);

	/**
	 * 根据id集合批量删除记录
	 * @param ids
	 * @return
	 */
	public int batchDelete(String[] ids);
	
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
}
