package analyse.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import analyse.constants.AppConfig;
import analyse.constants.Constants.MainDimension;
import analyse.constants.Constants.SortWay;
import analyse.constants.Constants.TimeGranularity;
import analyse.item.AnalysisItem;
import analyse.service.PanoramaAnalysisService;
import analyse.utils.CheckUtils;
import analyse.utils.EsUtils;
import pool.Pool;

@Service
public class PanoramaAnalysisServiceImpl implements PanoramaAnalysisService {
	
	@Autowired
	AppConfig appConfig;
	
	@Override
	public Map<String, Object> getEventDetail(String id) {
		GetResponse getResponse = Pool.getElasticsearchHelper().get(appConfig.getEsIndexEvent(), 
				appConfig.getEsTypeEvent(), id);
		Map<String, Object> source = getResponse.getSource();
		return source;
	}

	@Override
	public Map<String, Object> getEventStatisticsMainDimension(AnalysisItem analysisItem) throws ParseException {
		MainDimension mainDimension = analysisItem.getMainDimension();
		TimeGranularity timeGranularity = analysisItem.getAnalysisFilter().getTimeGranularity();
		SortWay order = analysisItem.getOrder();
		AbstractAggregationBuilder agg = EsUtils.buildAgg(mainDimension , timeGranularity, order);
		BoolQueryBuilder filter = EsUtils.buildFilter(analysisItem.getAnalysisFilter());
		SearchResponse searchResponse = Pool.getElasticsearchHelper().getSearchResponse(
				new String[] {appConfig.getEsIndexEvent()}, new String [] {appConfig.getEsTypeEvent()}, 
				null, filter, null, 0, 0, null, null, null, new AbstractAggregationBuilder[] {agg});
		Aggregations aggResult = searchResponse.getAggregations();
		return EsUtils.parseAggResultMain(aggResult, mainDimension);
	}

	@Override
	public Map<String, Object> getEventStatisticsOtherDimension(AnalysisItem analysisItem) throws ParseException {
		SortWay order = analysisItem.getOrder();
		AbstractAggregationBuilder[] aggs = EsUtils.buildAggOthers(order);
		BoolQueryBuilder filter = EsUtils.buildFilter(analysisItem.getAnalysisFilter());
		SearchResponse searchResponse = Pool.getElasticsearchHelper().getSearchResponse(
				new String[] {appConfig.getEsIndexEvent()}, new String [] {appConfig.getEsTypeEvent()}, 
				null, filter, null, 0, 0, null, null, null, aggs);
		Aggregations aggResult = searchResponse.getAggregations();
		return EsUtils.parseAggResultOther(aggResult);
	}

	@Override
	public List<Map<String, Object>> getEventList(AnalysisItem analysisItem) throws ParseException {
		SortWay order = analysisItem.getOrder();
		BoolQueryBuilder filter = EsUtils.buildFilter(analysisItem.getAnalysisFilter());
		SearchResponse searchResponse = Pool.getElasticsearchHelper().getSearchResponse(
				new String[] {appConfig.getEsIndexEvent()}, new String [] {appConfig.getEsTypeEvent()}, 
				null, filter, null, 0, 2^32 - 1, order.getPath(), EsUtils.getSortOrder(order), 60000L, null);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (SearchHit searchHit : searchResponse.getHits().getHits()) {
			result.add(searchHit.getSource());
		}
		return result;
	}

	@Override
	public Map<String, Object> getEventStatisticsAll() throws ParseException {
		AnalysisItem analysisItem = CheckUtils.getInitItem();
		SortWay order = analysisItem.getOrder();
		AbstractAggregationBuilder[] aggsOthers = EsUtils.buildAggOthers(order);
		AbstractAggregationBuilder[] aggsMains = EsUtils.buildAggs(order, analysisItem.getAnalysisFilter().getTimeGranularity());
		BoolQueryBuilder filter = EsUtils.buildFilter(analysisItem.getAnalysisFilter());
		AbstractAggregationBuilder[] aggs = (AbstractAggregationBuilder[]) ArrayUtils.addAll(aggsMains, aggsOthers);
		SearchResponse searchResponse = Pool.getElasticsearchHelper().getSearchResponse(
				new String[] {appConfig.getEsIndexEvent()}, new String [] {appConfig.getEsTypeEvent()}, 
				null, filter, null, 0, 0, null, null, null, aggs);
		Aggregations aggResult = searchResponse.getAggregations();
		Map<String, Object> result = EsUtils.parseAggResultMain(aggResult);
		result.putAll(EsUtils.parseAggResultOther(aggResult));
		return result;
	}

}
