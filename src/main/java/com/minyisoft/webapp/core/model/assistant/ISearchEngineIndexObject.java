package com.minyisoft.webapp.core.model.assistant;

import com.minyisoft.webapp.core.model.IModelObject;

/**
 * @author qingyong_ou 搜索引擎索引对象接口
 */
public interface ISearchEngineIndexObject extends IModelObject {
	/**
	 * 获取需索引属性集合
	 * 
	 * @return
	 */
	public String[] getIndexProperties();

	/**
	 * 是否满足索引条件
	 * 
	 * @return
	 */
	public boolean isIndexEnabled();
}
