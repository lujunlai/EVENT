package analyse.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import analyse.constants.Constants;
import analyse.constants.Constants.MainDimension;
import analyse.constants.Constants.OtherDimension;
import analyse.constants.Constants.SortWay;
import analyse.constants.Constants.TimeGranularity;
import analyse.item.AnalysisFilter;
import analyse.item.AnalysisTime;

public final class EsUtils {
	
	/**
	 * 根据analysisFilter对象生成对应的filter
	 * @param analysisFilter
	 * @return
	 * @throws ParseException
	 */
	public static BoolQueryBuilder buildFilter(AnalysisFilter analysisFilter) throws ParseException {
		if (analysisFilter == null)
			return null;
		
		BoolQueryBuilder filter = QueryBuilders.boolQuery();
		
		if (analysisFilter.getType() != null) {
			BoolQueryBuilder typeFilter = QueryBuilders.boolQuery();
			for (String filterType : analysisFilter.getType()) {
				typeFilter.should(QueryBuilders.termQuery(MainDimension.TYPE.getPath(), filterType));
			}
			filter.must(typeFilter);
		}
		
		if (analysisFilter.getArea() != null) {
			BoolQueryBuilder areaFilter = QueryBuilders.boolQuery();
			for (String filterArea : analysisFilter.getArea()) {
				areaFilter.should(QueryBuilders.termQuery(MainDimension.AREA.getPath(), filterArea));
			}
			filter.must(areaFilter);
		}
		
		if (analysisFilter.getGoodsType() != null) {
			BoolQueryBuilder goodsTypeFilter = QueryBuilders.boolQuery();
			for (String filterGoodsType : analysisFilter.getGoodsType()) {
				goodsTypeFilter.should(QueryBuilders.termQuery(MainDimension.GOODSTYPE.getPath(), filterGoodsType));
			}
			filter.must(goodsTypeFilter);
		}
		
		if (analysisFilter.getTime() != null) {
			BoolQueryBuilder timeFilter = QueryBuilders.boolQuery();
			for (AnalysisTime analysisTime : analysisFilter.getTime()) {
				timeFilter.should(QueryBuilders.rangeQuery(MainDimension.TIME.getPath())
						.format(analysisFilter.getTimeGranularity().getFormat()).from(analysisTime.getStartTime())
						.to(getNextTime(analysisTime.getEndTime(), analysisFilter.getTimeGranularity())).includeUpper(false));
			}
			filter.must(timeFilter);
		}
		
		return filter;
	}
	
	/**
	 * 根据参数生成对应的agg
	 * @param mainDimension
	 * @param timeGranularity
	 * @param order
	 * @return
	 */
	public static AbstractAggregationBuilder buildAgg(MainDimension mainDimension, TimeGranularity timeGranularity, SortWay order){
		String name = mainDimension.name().toLowerCase();
		String field = mainDimension.getPath();
		
		switch (mainDimension) {
		case TIME:
			DateHistogramInterval interval = getInterval(timeGranularity);
			return AggregationBuilders.dateHistogram(name).interval(interval).field(field).format(timeGranularity.getFormat());
		default: 
			return AggregationBuilders.terms(name).field(field).order(getTermOrder(order));
		}
	}
	
	/**
	 * 获取其他维度的aggs
	 * @param order
	 * @return
	 */
	public static AbstractAggregationBuilder[] buildAggOthers(SortWay order){
		int length = OtherDimension.values().length;
		AbstractAggregationBuilder[] aggs = new AbstractAggregationBuilder[length];
		
		for (int i = 0; i < length; i ++) {
			OtherDimension otherDimension = OtherDimension.values()[i];
			String name = otherDimension.name().toLowerCase();
			String field = otherDimension.getPath();
			if (otherDimension == OtherDimension.RISK) {
				aggs[i] = AggregationBuilders.avg(name).field(field);
			}
			else {
				aggs[i] = AggregationBuilders.terms(name).field(field).order(getTermOrder(order));
			}
		}
		
		return aggs;
	}
	
	/**
	 * 获取主维度的aggs
	 * @param order
	 * @return
	 */
	public static AbstractAggregationBuilder[] buildAggs(SortWay order, TimeGranularity timeGranularity){
		int length = MainDimension.values().length;
		AbstractAggregationBuilder[] aggs = new AbstractAggregationBuilder[length];
		
		for (int i = 0; i < length; i ++) {
			MainDimension mainDimension = MainDimension.values()[i];
			String name = mainDimension.name().toLowerCase();
			String field = mainDimension.getPath();
			if (mainDimension == MainDimension.TIME) {
				DateHistogramInterval interval = getInterval(timeGranularity);
				aggs[i] = AggregationBuilders.dateHistogram(name).interval(interval).field(field).format(timeGranularity.getFormat());
			
			}
			else
				aggs[i] = AggregationBuilders.terms(name).field(field).order(getTermOrder(order));
		}
		
		return aggs;
	}
	
	/**
	 * 解析主维度聚合结果
	 * @param aggResult
	 * @param mainDimension
	 * @return
	 */
	public static Map<String, Object> parseAggResultMain(Aggregations aggResult, MainDimension mainDimension){
		String name = mainDimension.name().toLowerCase();
		String displayName = mainDimension.getName();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMain = new HashMap<String, Object>();
		Map<String, Object> aggResultFormat = new LinkedHashMap<String, Object>();
		resultMain.put(Constants.displayName, displayName);
		if(isAggResultNull(aggResult, name)) {
			MultiBucketsAggregation multiBucketsAggResult = (MultiBucketsAggregation)aggResult.get(name);
			for (Bucket bucket : multiBucketsAggResult.getBuckets()) {
				String key = bucket.getKeyAsString();
				Object value = bucket.getDocCount();
				aggResultFormat.put(key, value);
			}
		}
		resultMain.put(Constants.statistics, aggResultFormat);
		result.put(name, resultMain);
		return result;
	}
	
	/**
	 * 解析主维度聚合结果
	 * @param aggResult
	 * @return
	 */
	public static Map<String, Object> parseAggResultMain(Aggregations aggResult){
		Map<String, Object> result = new HashMap<String, Object>();
		
		for (MainDimension mainDimension : MainDimension.values()) {
			String name = mainDimension.name().toLowerCase();
			String displayName = mainDimension.getName();
			if(!isAggResultNull(aggResult, name))
				continue;
			MultiBucketsAggregation multiBucketsAggResult = (MultiBucketsAggregation)aggResult.get(name);
			Map<String, Object> resultMain = new HashMap<String, Object>();
			Map<String, Object> aggResultFormat = new LinkedHashMap<String, Object>();
			for (Bucket bucket : multiBucketsAggResult.getBuckets()) {
				String key = bucket.getKeyAsString();
				Object value = bucket.getDocCount();
				aggResultFormat.put(key, value);
			}
			resultMain.put(Constants.displayName, displayName);
			resultMain.put(Constants.statistics, aggResultFormat);
			result.put(name, resultMain);
		}
		
		return result;
	}
	
	/**
	 * 解析其他维度聚合结果
	 * @param aggResult
	 * @return
	 */
	public static Map<String, Object> parseAggResultOther(Aggregations aggResult){
		Map<String, Object> result = new HashMap<String, Object>();
		
		for (OtherDimension otherDimension : OtherDimension.values()) {
			String name = otherDimension.name().toLowerCase();
			String displayName = otherDimension.getName();
			if(!isAggResultNull(aggResult, name))
				continue;
			Map<String, Object> resultMain = new HashMap<String, Object>();
			if (otherDimension == OtherDimension.RISK) {
				Avg avgAggResult = (Avg)aggResult.get(name);
				Double aggResultFormat = avgAggResult.getValue();
				resultMain.put(Constants.statistics, aggResultFormat);
			}
			else {
				Terms termAggResult = (Terms)aggResult.get(name);
				Map<String, Object> aggResultFormat = new LinkedHashMap<String, Object>();
				for (Bucket bucket : termAggResult.getBuckets()) {
					String key = bucket.getKeyAsString();
					Object value = bucket.getDocCount();
					aggResultFormat.put(key, value);
				}
				resultMain.put(Constants.statistics, aggResultFormat);
			}
			resultMain.put(Constants.displayName, displayName);
			result.put(name, resultMain);
		}
		
		return result;
	}
	
	/**
	 * 将SortWay转换成es的SortOrder
	 * @param order
	 * @return
	 */
	public static SortOrder getSortOrder(SortWay order) {
		switch (order) {
		case ORDERED: return SortOrder.ASC;
		case REVERSED: return SortOrder.DESC;
		default: return SortOrder.DESC;
		}
	}
	
	
	
	/**
	 * 根据时间粒度获取截止时间
	 * @param time
	 * @param timeGranularity
	 * @return
	 * @throws ParseException
	 */
	private static String getNextTime(String time, TimeGranularity timeGranularity) throws ParseException  {
		SimpleDateFormat sdf = new SimpleDateFormat(timeGranularity.getFormat());
		Date date = sdf.parse(time);
		Calendar calendar = new GregorianCalendar(); 
	    calendar.setTime(date);
	    int field = Calendar.DATE;
	    int delay = 1;
	    switch (timeGranularity) {
	    case DAY: break;
	    case WEEK: delay = 7; break;
	    case MONTH: field = Calendar.MONTH; break;
	    case YEAR: field = Calendar.YEAR; break;
	    }
	    calendar.add(field, delay); //把日期往后增加一天.整数往后推,负数往前移动
	    return sdf.format(calendar.getTime());
	}
	
	/**
	 * 将TimeGranularity转换成es中的interval
	 * @param timeGranularity
	 * @return
	 */
	private static DateHistogramInterval getInterval(TimeGranularity timeGranularity) {
		DateHistogramInterval interval;
		switch (timeGranularity) {
		case DAY: interval = DateHistogramInterval.DAY; break;
		case WEEK: interval = DateHistogramInterval.WEEK; break;
		case MONTH: interval = DateHistogramInterval.MONTH; break;
		case YEAR: interval = DateHistogramInterval.YEAR; break;
		default: interval = DateHistogramInterval.DAY;
		}
		return interval;
	}
	
	/**
	 * 将SortWay转换成es中的Order.term
	 * @param order
	 * @return
	 */
	private static Order getTermOrder(SortWay order) {
		switch (order) {
		case ORDERED: return Order.count(true);
		case REVERSED: return Order.count(false);
		default: return Order.count(false);
		}
	}
	
	/**
	 * 判断是否聚合存在
	 * @param aggResult
	 * @param name
	 * @return
	 */
	private static boolean isAggResultNull(Aggregations aggResult, String name) {
		if (aggResult.get(name) == null) {
			Logger logger = LoggerFactory.getLogger(Utils.class);
			logger.info("{} dimension is missing", name);
			return false;
		}
		return true;
	}
	
}
