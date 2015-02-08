package com.minyisoft.webapp.core.utils.elasticsearch;

import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.minyisoft.webapp.core.model.assistant.search.CustomScriptField;
import com.minyisoft.webapp.core.model.assistant.search.ISearchDocObject;
import com.minyisoft.webapp.core.model.assistant.search.ISearchType;
import com.minyisoft.webapp.core.model.criteria.SearchCriteria;
import com.minyisoft.webapp.core.persistence.ISearchEngineDao;
import com.minyisoft.webapp.core.utils.mapper.json.JsonMapper;

/**
 * ElasticSearch搜索引擎实现类
 * 
 * @author qingyong_ou
 */
public class ElasticSearchDao implements ISearchEngineDao {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private String index;
	private TransportClient client;

	public ElasticSearchDao(String clusterName, String[] nodes, String index) {
		this.index = index;
		if (StringUtils.isNotBlank(clusterName) && ArrayUtils.isNotEmpty(nodes)) {
			Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
			client = new TransportClient(settings);
			for (String node : nodes) {
				String[] nodeDetail = StringUtils.split(node, ":");
				client.addTransportAddress(new InetSocketTransportAddress(nodeDetail[0], Integer
						.parseInt(nodeDetail[1])));
			}
		}
	}

	public void destroy() {
		if (client != null) {
			client.close();
		}
	}

	@Override
	public void createIndexType(ISearchType searchType) {
		if (searchType != null) {
			boolean indexEdists = false;
			// 若索引存在，先删除索引类别
			IndicesExistsResponse response = client.admin().indices().prepareExists(index).execute().actionGet();
			if (response.isExists()) {
				indexEdists = true;
				delete(searchType);
			}
			if (searchType.getFieldsProperties() != null) {
				XContentBuilder builder = null;
				try {
					builder = XContentFactory.jsonBuilder().startObject().startObject(searchType.getTypeName())
							.startObject("properties");
					ISearchType.FieldProperties fieldProperties = null;
					for (String key : searchType.getFieldsProperties().keySet()) {
						builder.startObject(key);
						fieldProperties = searchType.getFieldsProperties().get(key);
						builder.field("type", fieldProperties.getFieldType().toString());
						if (fieldProperties.getIndexOprtType() != null) {
							builder.field("index", fieldProperties.getIndexOprtType().toString());
						}
						builder.endObject();
					}
					builder.endObject().endObject().endObject();

					if (builder != null) {
						if (!indexEdists) {
							client.admin().indices().prepareCreate(index)
									.addMapping(searchType.getTypeName(), builder.string()).execute().actionGet();
						} else {
							PutMappingRequest mappingRequest = Requests.putMappingRequest(index)
									.type(searchType.getTypeName()).source(builder);
							client.admin().indices().putMapping(mappingRequest).actionGet();
						}
					}
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public void delete(ISearchType searchType, String... ids) {
		notNull(searchType, "未指定待删除索引类型");
		if (ArrayUtils.isEmpty(ids)) {
			TypesExistsResponse typesExistsResponse = client.admin().indices().prepareTypesExists(index)
					.setTypes(searchType.getTypeName()).execute().actionGet();
			if (typesExistsResponse.isExists()) {
				DeleteMappingResponse deleteMappingResponse = client.admin().indices()
						.deleteMapping(new DeleteMappingRequest(index).types(searchType.getTypeName())).actionGet();
				deleteMappingResponse.getHeaders();
			}
		} else {
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			for (String id : ids) {
				bulkRequest.add(client.prepareDelete().setIndex(index).setType(searchType.getTypeName()).setId(id));
			}
			BulkResponse bulkResponse = bulkRequest.execute().actionGet();
			if (bulkResponse.hasFailures()) {
				Iterator<BulkItemResponse> iterator = bulkResponse.iterator();
				while (iterator.hasNext()) {
					BulkItemResponse response = iterator.next();
					if (response.isFailed()) {
						logger.error(response.getFailureMessage());
					}
				}
			}
		}
	}

	@Override
	public void index(ISearchDocObject... indexObjects) {
		if (ArrayUtils.isNotEmpty(indexObjects)) {
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			ISearchType searchType;
			for (ISearchDocObject object : indexObjects) {
				if (object != null && object.isIdPresented() && object.isIndexable()) {
					searchType = object.getSearchType();
					bulkRequest.add(client.prepareIndex(index, searchType.getTypeName(), object.getId()).setSource(
							JsonMapper.NON_DEFAULT_MAPPER.toJson(searchType.getIndexFields(object))));
				}
			}
			if (bulkRequest.numberOfActions() > 0) {
				BulkResponse bulkResponse = bulkRequest.execute().actionGet();
				if (bulkResponse.hasFailures()) {
					Iterator<BulkItemResponse> iterator = bulkResponse.iterator();
					while (iterator.hasNext()) {
						BulkItemResponse response = iterator.next();
						if (response.isFailed()) {
							logger.error(response.getFailureMessage());
						}
					}
				}
			}
		}
	}

	@Override
	public SearchHits search(ISearchType searchType, SearchCriteria searchCriteria) {
		if (searchType != null) {
			SearchRequestBuilder builder = client.prepareSearch(index).setTypes(searchType.getTypeName());
			if (searchCriteria.getPageDevice() != null) {
				builder.setFrom(searchCriteria.getPageDevice().getStartRowNumberOfCurrentPage() - 1);
				builder.setSize(searchCriteria.getPageDevice().getRecordsPerPage());
			}
			QueryBuilder queryBuilder = null;
			if (StringUtils.isNotBlank(searchCriteria.getKeyword())) {
				if (ArrayUtils.isNotEmpty(searchCriteria.getQueryFields())) {
					if (StringUtils.contains(searchCriteria.getKeyword(), "*")
							|| StringUtils.contains(searchCriteria.getKeyword(), "?")) {
						builder.addHighlightedField(searchCriteria.getQueryFields()[0]);
						queryBuilder = QueryBuilders.wildcardQuery(searchCriteria.getQueryFields()[0],
								searchCriteria.getKeyword());
					} else {
						for (String field : searchCriteria.getQueryFields()) {
							builder.addHighlightedField(field);
						}
						queryBuilder = QueryBuilders.multiMatchQuery(searchCriteria.getKeyword(),
								searchCriteria.getQueryFields());
					}
				} else {
					for (String field : searchType.getKeywordFields()) {
						builder.addHighlightedField(field);
					}
					queryBuilder = QueryBuilders.multiMatchQuery(searchCriteria.getKeyword(),
							searchType.getKeywordFields());
				}
			}
			if (StringUtils.isNotBlank(searchCriteria.getCustomScoreScript())) {
				queryBuilder = QueryBuilders.functionScoreQuery(queryBuilder).add(
						ScoreFunctionBuilders.scriptFunction(searchCriteria.getCustomScoreScript()));
			}
			builder.setQuery(queryBuilder);
			if (CollectionUtils.isNotEmpty(searchCriteria.getFilterList())) {
				builder.setPostFilter(FilterBuilders.andFilter(searchCriteria.getFilterList().toArray(
						new FilterBuilder[searchCriteria.getFilterList().size()])));
			}
			if (CollectionUtils.isNotEmpty(searchCriteria.getOrderList())) {
				for (SortBuilder sortBuilder : searchCriteria.getOrderList()) {
					builder.addSort(sortBuilder);
				}
			}
			if (CollectionUtils.isNotEmpty(searchCriteria.getScriptFields())) {
				for (CustomScriptField field : searchCriteria.getScriptFields()) {
					builder.addScriptField(field.getName(), field.getScript());
				}
			}
			return builder.execute().actionGet().getHits();
		}
		return null;
	}

	@Override
	public Map<String, Facet> searchFacets(ISearchType searchType, SearchCriteria searchCriteria) {
		if (searchType != null && CollectionUtils.isNotEmpty(searchCriteria.getFacetsList())) {
			SearchRequestBuilder builder = client.prepareSearch(index).setTypes(searchType.getTypeName());
			if (StringUtils.isNotBlank(searchCriteria.getKeyword())) {
				if (ArrayUtils.isNotEmpty(searchCriteria.getQueryFields())) {
					builder.setQuery(QueryBuilders.multiMatchQuery(searchCriteria.getKeyword(),
							searchCriteria.getQueryFields()));
				} else {
					builder.setQuery(QueryBuilders.multiMatchQuery(searchCriteria.getKeyword(),
							searchType.getKeywordFields()));
				}
			}
			for (FacetBuilder facetBuilder : searchCriteria.getFacetsList()) {
				if (CollectionUtils.isNotEmpty(searchCriteria.getFilterList())) {
					facetBuilder.facetFilter(FilterBuilders.andFilter(searchCriteria.getFilterList().toArray(
							new FilterBuilder[searchCriteria.getFilterList().size()])));
				}
				builder.addFacet(facetBuilder);
			}
			SearchResponse response = builder.execute().actionGet();
			if (response.getFacets() != null) {
				return response.getFacets().facetsAsMap();
			}
		}
		return null;
	}
}
