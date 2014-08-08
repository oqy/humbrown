package com.minyisoft.webapp.core.persistence;

import java.util.List;

import com.minyisoft.webapp.core.model.IModelObject;
import com.minyisoft.webapp.core.model.criteria.BaseCriteria;

public interface BaseDao<T extends IModelObject, C extends BaseCriteria> {
	/**
	 * 根据id查找数据库记录并转换为对应对象
	 * 
	 * @param id
	 * @return
	 */
	T getEntity(String id);

	/**
	 * 获取满足过滤条件的记录总数
	 * 
	 * @param baseCriteria
	 * @return
	 */
	int countEntity(C criteria);

	/**
	 * 根据过滤条件查找，返回满足条件的所有记录
	 * 
	 * @param baseCriteria
	 * @return
	 */
	List<T> getEntityCollection(C criteria);

	/**
	 * 新增记录
	 * 
	 * @param info
	 */
	void insertEntity(T info);

	/**
	 * 更新（保存）记录
	 * 
	 * @param info
	 * @return
	 */
	int updateEntity(IModelObject info);

	/**
	 * 根据id集合批量删除记录
	 * 
	 * @param ids
	 * @return
	 */
	int batchDelete(List<String> ids);
}
