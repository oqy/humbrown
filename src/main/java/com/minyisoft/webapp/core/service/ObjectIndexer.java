package com.minyisoft.webapp.core.service;

import com.minyisoft.webapp.core.model.assistant.search.ISearchDocObject;
import com.minyisoft.webapp.core.model.assistant.search.ISearchType;

/**
 * @author yongan_cui 搜索引擎索引服务接口
 */
public interface ObjectIndexer {

	/**
	 * 创建索引类别，若已存在指定类别，则先删除该类别及对应索引数据
	 * 
	 * @param searchType
	 */
	void createIndexType(ISearchType searchType);

	/**
	 * 索引对象所有设定属性
	 * 
	 * @param indexObjects
	 */
	void index(ISearchDocObject... indexObjects);

	/**
	 * 删除索引
	 * 
	 * @param searchType
	 * @param ids
	 */
	void delete(ISearchType searchType, String... ids);
}
