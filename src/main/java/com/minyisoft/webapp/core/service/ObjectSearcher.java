package com.minyisoft.webapp.core.service;

import java.util.Map;

import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.Facet;

import com.minyisoft.webapp.core.model.assistant.search.ISearchType;
import com.minyisoft.webapp.core.model.criteria.SearchCriteria;

/**
 * @author yongan_cui 搜索引擎搜索服务接口
 */
public interface ObjectSearcher extends ObjectIndexer {

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
