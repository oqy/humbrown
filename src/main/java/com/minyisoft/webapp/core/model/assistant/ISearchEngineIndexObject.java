package com.minyisoft.webapp.core.model.assistant;

/**
 * @author qingyong_ou 搜索引擎索引对象接口
 */
public interface ISearchEngineIndexObject {
	/**
	 * 获取索引主键
	 * 
	 * @return
	 */
	public String getId();

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
