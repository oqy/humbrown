package com.minyisoft.webapp.core.service.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.AbstractFacetBuilder;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.minyisoft.webapp.core.model.assistant.search.ISearchDocObject;
import com.minyisoft.webapp.core.model.assistant.search.ISearchType;
import com.minyisoft.webapp.core.model.criteria.SearchCriteria;
import com.minyisoft.webapp.core.service.ISearchBase;
import com.minyisoft.webapp.core.utils.mapper.json.JsonMapper;

public abstract class SearchBaseImpl implements ISearchBase {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private JsonMapper jsonMapper=JsonMapper.nonDefaultMapper();
	
	/**
	 * 获取搜索客户端
	 * @return
	 */
	protected abstract TransportClient getClient();
	
	/**
	 * 获取待搜索索引名
	 * @return
	 */
	protected abstract String getSearchIndex();
	
	/**
	 * 记录/统计搜索关键字
	 * @param keyword
	 */
	protected abstract void markSearchKeyword(String keyword);
	
	@Override
	public void createIndexType(ISearchType searchType) {
		if(searchType!=null){
			boolean indexEdists=false;
			// 若索引存在，先删除索引类别
			IndicesExistsResponse response=getClient().admin().indices().prepareExists(getSearchIndex()).execute().actionGet();
			if(response.isExists()){
				indexEdists=true;
				delete(searchType);
			}
			if(searchType.getFieldsProperties()!=null){
			    XContentBuilder builder=null;
				try {
					builder = XContentFactory.jsonBuilder().startObject().startObject(searchType.getTypeName()).startObject("properties");
					ISearchType.FieldProperties fieldProperties=null;
					for(String key:searchType.getFieldsProperties().keySet()){
						builder.startObject(key);
						fieldProperties=searchType.getFieldsProperties().get(key);
						builder.field("type",fieldProperties.getFieldType().toString());
						if(fieldProperties.getIndexOprtType()!=null){
							builder.field("index",fieldProperties.getIndexOprtType().toString());
						}
						builder.endObject();
					}                
					builder.endObject().endObject().endObject();
					
					if(builder!=null){
						if(!indexEdists){
							getClient().admin().indices().prepareCreate(getSearchIndex()).addMapping(searchType.getTypeName(), builder.string()).execute().actionGet();
						}else{
							PutMappingRequest mappingRequest = Requests.putMappingRequest(getSearchIndex()).type(searchType.getTypeName()).source(builder);  
							getClient().admin().indices().putMapping(mappingRequest).actionGet();  
						}
					}
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
	}
	
	@Override
	public void delete(ISearchType searchType,String... ids){
		if(ArrayUtils.isEmpty(ids)){
			if(searchType!=null){
				TypesExistsResponse typesExistsResponse=getClient().admin().indices().prepareTypesExists(getSearchIndex()).setTypes(searchType.getTypeName()).execute().actionGet();
				if(typesExistsResponse.isExists()){
					DeleteMappingResponse deleteMappingResponse=getClient().admin().indices().deleteMapping(new DeleteMappingRequest(getSearchIndex()).type(searchType.getTypeName())).actionGet();
					deleteMappingResponse.getHeaders();
				}
			}
		}else{
			BulkRequestBuilder bulkRequest = getClient().prepareBulk();
			for(String id:ids){
				bulkRequest.add(getClient().prepareDelete().setIndex(getSearchIndex()).setType(searchType.getTypeName()).setId(id));
			}
			BulkResponse bulkResponse = bulkRequest.execute().actionGet();
			if (bulkResponse.hasFailures()) {
				Iterator<BulkItemResponse> iterator=bulkResponse.iterator();
				while(iterator.hasNext()){
					BulkItemResponse response=iterator.next();
					if(response.isFailed()){
						logger.error(response.getFailureMessage());
					}
				}
			}
		}
	}

	@Override
	public void index(ISearchDocObject... indexObjects){
		if(ArrayUtils.isNotEmpty(indexObjects)){
			BulkRequestBuilder bulkRequest = getClient().prepareBulk();
			ISearchType searchType;
			for(ISearchDocObject object:indexObjects){
				if(object!=null&&object.isIdPresented()&&object.isIndexable()){
					searchType=object.getSearchType();
					bulkRequest.add(getClient().prepareIndex(getSearchIndex(), searchType.getTypeName(), object.getId())
						        .setSource(jsonMapper.toJson(searchType.getIndexFields(object))));
				}
			}
			if(bulkRequest.numberOfActions()>0){
				BulkResponse bulkResponse = bulkRequest.execute().actionGet();
				if (bulkResponse.hasFailures()) {
					Iterator<BulkItemResponse> iterator=bulkResponse.iterator();
					while(iterator.hasNext()){
						BulkItemResponse response=iterator.next();
						if(response.isFailed()){
							logger.error(response.getFailureMessage());
						}
					}
				}
			}
		}
	}
	
	@Override
	public SearchHits search(ISearchType searchType, SearchCriteria searchCriteria) {
		if(searchType!=null){
			SearchRequestBuilder builder=getClient().prepareSearch(getSearchIndex()).setTypes(searchType.getTypeName());
			if(searchCriteria.getPageDevice()!=null){
				builder.setFrom(searchCriteria.getPageDevice().getStartRowNumberOfCurrentPage()-1);
				builder.setSize(searchCriteria.getPageDevice().getRecordsPerPage());
			}
			if(StringUtils.isNotBlank(searchCriteria.getKeyword())){
				markSearchKeyword(searchCriteria.getKeyword());
				if(ArrayUtils.isNotEmpty(searchCriteria.getQueryFields())){
					for(String field:searchCriteria.getQueryFields()){
						builder.addHighlightedField(field);
					}
					builder.setQuery(QueryBuilders.multiMatchQuery(searchCriteria.getKeyword(),searchCriteria.getQueryFields()));
				}else{
					for(String field:searchType.getKeywordFields()){
						builder.addHighlightedField(field);
					}
					builder.setQuery(QueryBuilders.multiMatchQuery(searchCriteria.getKeyword(),searchType.getKeywordFields()));
				}
			}
			if(CollectionUtils.isNotEmpty(searchCriteria.getFilterList())){
				builder.setFilter(FilterBuilders.andFilter(searchCriteria.getFilterList().toArray(new FilterBuilder[searchCriteria.getFilterList().size()])));
			}
			if(CollectionUtils.isNotEmpty(searchCriteria.getOrderList())){
				for(SortBuilder sortBuilder:searchCriteria.getOrderList()){
					builder.addSort(sortBuilder);
				}
			}
			return builder.execute().actionGet().hits();
		}
		return null;
	}

	@Override
	public Map<String, Facet> searchFacets(ISearchType searchType, SearchCriteria searchCriteria) {
		if(searchType!=null&&CollectionUtils.isNotEmpty(searchCriteria.getFacetsList())){
			SearchRequestBuilder builder=getClient().prepareSearch(getSearchIndex()).setTypes(searchType.getTypeName());
			if(StringUtils.isNotBlank(searchCriteria.getKeyword())){
				if(ArrayUtils.isNotEmpty(searchCriteria.getQueryFields())){
					builder.setQuery(QueryBuilders.multiMatchQuery(searchCriteria.getKeyword(),searchCriteria.getQueryFields()));
				}else{
					builder.setQuery(QueryBuilders.multiMatchQuery(searchCriteria.getKeyword(),searchType.getKeywordFields()));
				}
			}
			for(AbstractFacetBuilder facetBuilder:searchCriteria.getFacetsList()){
				if(CollectionUtils.isNotEmpty(searchCriteria.getFilterList())){
					facetBuilder.facetFilter(FilterBuilders.andFilter(searchCriteria.getFilterList().toArray(new FilterBuilder[searchCriteria.getFilterList().size()])));
				}
				builder.addFacet(facetBuilder);
			}
			SearchResponse response=builder.execute().actionGet();
			if(response.facets()!=null){
				return response.facets().facetsAsMap();
			}
		}
		return null;
	}
}
