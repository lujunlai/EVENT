package analyse.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import analyse.item.AnalysisItem;

public interface PanoramaAnalysisService {
	
	public Map<String,Object> getEventStatisticsMainDimension(AnalysisItem analysisItem) throws ParseException;
	
	public Map<String, Object> getEventStatisticsOtherDimension(AnalysisItem analysisItem) throws ParseException;
	
	public Map<String, Object> getEventStatisticsAll() throws ParseException;
	
	public List<Map<String,Object>> getEventList(AnalysisItem analysisItem) throws ParseException;
	
	public Map<String,Object> getEventDetail(String id);
	
}
