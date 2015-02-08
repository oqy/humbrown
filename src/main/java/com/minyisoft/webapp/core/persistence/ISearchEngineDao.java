package com.minyisoft.webapp.core.persistence;

import java.util.Map;

import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.Facet;

import com.minyisoft.webapp.core.model.assistant.search.ISearchDocObject;
import com.minyisoft.webapp.core.model.assistant.search.ISearchType;
import com.minyisoft.webapp.core.model.criteria.SearchCriteria;

/**
 * @author yongan_cui 搜索DAO
 */
public interface ISearchEngineDao {

	/**
	 * 创建索引类别，若已存在指定类别，则先删除该类别及对应索引数据
	 * 
	 * @param searchType
	 */
	void createIndexType(ISearchType searchType);

	/**
	 * 索引对象所有属性
	 * 
	 * @param indexObjects
	 * @return
	 */
	void index(ISearchDocObject... indexObjects);

	/**
	 * 根据对象id集合删除索引,id集合为空时删除索引类别
	 * 
	 * @param searchType
	 * @param ids
	 * @return
	 */
	void delete(ISearchType searchType, String... ids);

	/**
	 * 根据指定关键字，对指定对象指定属性内容进行查询，按指定分页返回结果 默认高亮显示关键字：isHighlight=true
	 * 
	 * @param searchType
	 * @param searchCriteria
	 * @return
	 */
	SearchHits search(ISearchType searchType, SearchCriteria searchCriteria);

	/**
	 * 根据指定关键字、过滤条件、排序，对指定对象指定属性内容进行查询，按指定分页返回结果，且统计指定的字段
	 * 
	 * @param searchType
	 * @param searchCriteria
	 * @return
	 */
	Map<String, Facet> searchFacets(ISearchType searchType, SearchCriteria searchCriteria);
}
