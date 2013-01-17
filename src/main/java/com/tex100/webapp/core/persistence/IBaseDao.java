package com.tex100.webapp.core.persistence;

import java.util.List;

import com.tex100.webapp.core.model.CoreBaseInfo;
import com.tex100.webapp.core.model.criteria.BaseCriteria;

public interface IBaseDao<T extends CoreBaseInfo, C extends BaseCriteria> {
	/**
	 * 根据id查找数据库记录并转换为对应对象
	 * 
	 * @param id
	 * @return
	 */
	public T getEntity(String id);

	/**
	 * 获取满足过滤条件的记录总数
	 * 
	 * @param baseCriteria
	 * @return
	 */
	public int countEntity(C criteria);

	/**
	 * 根据过滤条件查找，返回满足条件的所有记录
	 * 
	 * @param baseCriteria
	 * @return
	 */
	public List<T> getEntityCollection(C criteria);

	/**
	 * 新增记录
	 * 
	 * @param info
	 */
	public void insertEntity(T info);

	/**
	 * 更新（保存）记录
	 * 
	 * @param info
	 * @return
	 */
	public int updateEntity(T info);

	/**
	 * 根据id集合批量删除记录
	 * 
	 * @param ids
	 * @return
	 */
	public int batchDelete(List<String> ids);
}
