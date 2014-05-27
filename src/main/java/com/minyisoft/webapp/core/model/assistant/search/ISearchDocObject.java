package com.minyisoft.webapp.core.model.assistant.search;

import com.minyisoft.webapp.core.model.IModelObject;

/**
 * @author qingyong_ou 可供搜索引擎索引的文档对象接口
 */
public interface ISearchDocObject extends IModelObject {
	/**
	 * 获取对应索引类别
	 * 
	 * @return
	 */
	public ISearchType getSearchType();

	/**
	 * 是否满足索引条件
	 * 
	 * @return
	 */
	public boolean isIndexable();

	/**
	 * 搜索权重
	 * 
	 * @return
	 */
	public int getSearchWeight();
}
