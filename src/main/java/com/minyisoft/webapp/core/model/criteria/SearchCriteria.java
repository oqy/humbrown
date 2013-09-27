package com.minyisoft.webapp.core.model.criteria;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.search.facet.AbstractFacetBuilder;
import org.elasticsearch.search.sort.SortBuilder;

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
	private List<AbstractFacetBuilder> facetsList;
	
	public SearchCriteria addFilter(FilterBuilder builder){
		if(builder!=null){
			if(filterList==null){
				filterList=new ArrayList<FilterBuilder>();
			}
			filterList.add(builder);
		}
		return this;
	}
	
	public SearchCriteria addSorter(SortBuilder builder){
		if(builder!=null){
			if(orderList==null){
				orderList=new ArrayList<SortBuilder>();
			}
			orderList.add(builder);
		}
		return this;
	}
	
	public SearchCriteria addFacet(AbstractFacetBuilder builder){
		if(builder!=null){
			if(facetsList==null){
				facetsList=new ArrayList<AbstractFacetBuilder>();
			}
			facetsList.add(builder);
		}
		return this;
	}
}
