package elasticsearch;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkIndexByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ElasticsearchHelper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7053635680615257416L;

	private String hosts;
	
	private String clusterName;
	
	private Settings settings;
	
	private TransportClient client;
	
	public void afterPropertiesSet() throws UnknownHostException {
		String[] hostArray = hosts.split(",");
		settings = Settings.settingsBuilder()
	            .put("cluster.name",clusterName)
	            .put("client.transport.sniff", true)
	            .build();
		client = TransportClient.builder().settings(settings).build();
		for(String host : hostArray){
			String ip = host.split(":")[0];
			Integer port = Integer.valueOf(host.split(":")[1]);
			client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), port)); 
		}
	}
	
	/**
	 * 获取一条数据
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public GetResponse get(String index ,String type, String id){
		return client.prepareGet(index, type, id).setOperationThreaded(true).get();//默认开个新的线程去处理，后面将不在加入setOperationThreaded
	}
	
	/**
	 * 新增一条数据
	 * @param index
	 * @param type
	 * @param json
	 * @return
	 */
	public IndexResponse index(String index, String type, String json){
		return client.prepareIndex(index, type).setSource(json).get();
	}

	/**
	 * 在制定id重新索引一条数据
	 * @param index
	 * @param type
	 * @param id
	 * @param json
	 * @return
	 */
	public IndexResponse index(String index, String type, String id, String json) {
		return client.prepareIndex(index, type, id).setSource(json).get();
	}

	/**
	 * 新增一条数据
	 * @param index
	 * @param type
	 * @param id
	 * @param obj
	 * @return
	 */
	public IndexResponse index(String index, String type,String id, Object... obj){
		return client.prepareIndex(index, type, id).setSource(obj).get();
	}

	/**
	 * 删除一条数据
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public DeleteResponse delete(String index, String type, String id){
		return client.prepareDelete(index, type, id).get();
	}

	/**
	 * 根据条件删除
	 * @param index
	 * @param query
	 * @return
	 */
	public BulkIndexByScrollResponse deleteByQuery(String index, QueryBuilder query){
		return DeleteByQueryAction.INSTANCE.newRequestBuilder(client).filter(query).source(index).get();
	}

	/**
	 * 根据条件删除，可以自定义onResponse, onFailure等操作
	 * 详细可以参考：https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-delete-by-query.html
	 * @param index
	 * @param query
	 * @param listener
	 */
	public void deleteByQuery(String index, QueryBuilder query, ActionListener<BulkIndexByScrollResponse> listener){
		DeleteByQueryAction.INSTANCE.newRequestBuilder(client).filter(query).source(index).execute(listener);
	}
	
	/**
	 * 更新一个数据
	 * @param index
	 * @param type
	 * @param id
	 * @param obj
	 * @return
	 */
	public UpdateResponse update(String index, String type, String id, Object obj){
		return client.prepareUpdate(index, type, id).setDoc(obj).get();
	}
	
	/**
	 * 批量获取文档
	 * @param index
	 * @param type
	 * @param ids
	 * @return
	 */
	public MultiGetResponse multiGet(String index, String type, String... ids){
		return client.prepareMultiGet().add(index, type, ids).get();
	}
	
	/**
	 * 查询
	 * @param indices index列表
	 * @param types type列表
	 * @param searchType 查询类型
	 * @param query 字段过滤
	 * @param postFilter 条件过滤
	 * @param from 开始索引
	 * @param size 查询pageSize
	 * @param sortField 排序字段
	 * @param order 顺序
	 * @param scrollKeepaliveMilliSeconds scroll keepalive的时间
	 * @return
	 */
	public SearchResponse search(String[] indices, String[] types, SearchType searchType,QueryBuilder query,
			QueryBuilder postFilter, Integer from, Integer size, String sortField, SortOrder order, 
			long scrollKeepaliveMilliSeconds){
		if(from == null){
			from = 0;
		}
		if(size == null){
			size = 20;
		}
		if(searchType==null){
			searchType = SearchType.DFS_QUERY_THEN_FETCH;
		}
		SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indices)
			.addSort(sortField, order)
			.setTypes(types)
			.setSearchType(searchType)
			.setQuery(query)
			.setScroll(new TimeValue(scrollKeepaliveMilliSeconds));
		if(postFilter != null){
			searchRequestBuilder.setPostFilter(postFilter);
		}
		searchRequestBuilder.setFrom(from);
		searchRequestBuilder.setSize(size);
		searchRequestBuilder.setExplain(true);
		return searchRequestBuilder.get();
	}
	
	/**
	 * 根据scrollId进行查询
	 * @param scrollId
	 * @param scrollKeepaliveMilliSeconds
	 * @return
	 */
	public SearchResponse searchScroll(String scrollId,long scrollKeepaliveMilliSeconds){
		return client.prepareSearchScroll(scrollId).setScroll(new TimeValue(scrollKeepaliveMilliSeconds)).execute().actionGet();
	}
	
	/**
	 * 组合查询
	 * @param builders
	 * @return
	 */
	public MultiSearchResponse multiSearch(SearchRequestBuilder... builders){
		MultiSearchRequestBuilder msrb = client.prepareMultiSearch();
		for(SearchRequestBuilder srb : builders){
			msrb.add(srb);
		}
		return msrb.get();
	}
	
	/**
	 * 自定义es搜索，包括query和agg
	 * @param client
	 * @param indices
	 * @param types
	 * @param searchType
	 * @param query
	 * @param postFilter
	 * @param from
	 * @param size
	 * @param sortField
	 * @param order
	 * @param scrollKeepaliveMilliSeconds
	 * @param aggregation
	 * @return
	 */
	public SearchResponse getSearchResponse(String[] indices, 
			String[] types, SearchType searchType, QueryBuilder query, QueryBuilder postFilter, 
			Integer from, Integer size, String sortField, SortOrder order, 
			Long scrollKeepaliveMilliSeconds, AbstractAggregationBuilder[] aggregations) {
		if(from == null){
			from = 0;
		}
		if(size == null){
			size = 20;
		}
		if(searchType == null){
			searchType = SearchType.DFS_QUERY_THEN_FETCH;
		}
		SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indices).setTypes(types).setSearchType(searchType);
		if (sortField != null)
			searchRequestBuilder.addSort(sortField, order).setScroll(new TimeValue(scrollKeepaliveMilliSeconds));
		if (query != null) {
			searchRequestBuilder.setQuery(query);
		}	
		if(postFilter != null) {
			searchRequestBuilder.setPostFilter(postFilter);
		}
		searchRequestBuilder.setFrom(from);
		searchRequestBuilder.setSize(size);
		if(aggregations != null)
			for (AbstractAggregationBuilder aggregation : aggregations)
				searchRequestBuilder.addAggregation(aggregation);
		return searchRequestBuilder.setExplain(true).execute().actionGet();
	}
	
	public String getHosts() {
		return hosts;
	}
	
	public void setHosts(String hosts) {
		this.hosts = hosts;
	}
	
	public String getClusterName() {
		return clusterName;
	}
	
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	
	public TransportClient getClient() {
		return client;
	}
	
	public void setClient(TransportClient client) {
		this.client = client;
	}
	
}

