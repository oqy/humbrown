package com.minyisoft.webapp.core.model.criteria;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import com.google.common.collect.Lists;
import com.minyisoft.webapp.core.model.assistant.search.CustomScriptField;

/**
 * @author yongan_cui 搜索引擎辅助对象-过滤、排序和统计
 */
@Getter
@Setter
public class SearchCriteria {
	// 搜索的关键字
	private String keyword;
	// 关键字搜索区域
	private String[] queryFields;
	// 分页器
	private PageDevice pageDevice;
	// 过滤列表
	private List<FilterBuilder> filterList;
	// 排序列表
	private List<SortBuilder> orderList;
	// 统计列表
	private List<FacetBuilder> facetsList;
	// script field列表
	private List<CustomScriptField> scriptFields;
	// 自定义_score脚本
	private String customScoreScript;

	public SearchCriteria addFilter(FilterBuilder builder) {
		if (builder != null) {
			if (filterList == null) {
				filterList = Lists.newArrayList();
			}
			filterList.add(builder);
		}
		return this;
	}

	public SearchCriteria addSorter(SortBuilder builder) {
		if (builder != null) {
			if (orderList == null) {
				orderList = Lists.newArrayList();
			}
			orderList.add(builder);
		}
		return this;
	}

	public SearchCriteria addFacet(FacetBuilder builder) {
		if (builder != null) {
			if (facetsList == null) {
				facetsList = Lists.newArrayList();
			}
			facetsList.add(builder);
		}
		return this;
	}

	public SearchCriteria addScriptField(CustomScriptField field) {
		if (field != null) {
			if (scriptFields == null) {
				scriptFields = Lists.newArrayList();
			}
			scriptFields.add(field);
		}
		return this;
	}
}
