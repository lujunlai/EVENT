package analyse.controller;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import analyse.item.AnalysisItem;
import analyse.service.PanoramaAnalysisService;
import analyse.utils.CheckUtils;
/**
 * 事件接口
 * @author ljl
 *
 */
@RestController
public class PanoramaAnalysisController {
	
	@Autowired
	private PanoramaAnalysisService panoramaAnalysisService;

    @RequestMapping(value = "/getEventStatistics", method = {RequestMethod.POST}, produces = "application/json")
	public Map<String, Object> getEventStatistics(@RequestBody AnalysisItem analysisItem) {
    	if (analysisItem.getMainDimension() == null)
    		return null;
    	CheckUtils.islegalAnalysisItem(analysisItem);
		try {
			return panoramaAnalysisService.getEventStatisticsMainDimension(analysisItem);
		} catch (ParseException e) {
			return null;
		}
	}

	@RequestMapping(value = "/getEventStatisticsAll", method = {RequestMethod.POST}, produces = "application/json")
	public Map<String, Object> getEventStatisticsAll() throws ParseException {
		return panoramaAnalysisService.getEventStatisticsAll();
	}

    @RequestMapping(value = "/getEventStatisticsOthers", method = {RequestMethod.POST}, produces = "application/json")
	public Map<String, Object> getEventStatisticsOthers(@RequestBody AnalysisItem analysisItem) {
    	CheckUtils.islegalAnalysisItem(analysisItem);
    	try {
			return panoramaAnalysisService.getEventStatisticsOtherDimension(analysisItem);
		} catch (ParseException e) {
			return null;
		}
	}
    
    @RequestMapping(value = "/getEventList", method = {RequestMethod.POST}, produces = "application/json")
	public List<Map<String, Object>> getEventList(@RequestBody AnalysisItem analysisItem) {
    	CheckUtils.islegalAnalysisItem(analysisItem);
		try {
			return panoramaAnalysisService.getEventList(analysisItem);
		} catch (ParseException e) {
			return null;
		}
	}
    
    @RequestMapping(value = "/getEventDetail", method = {RequestMethod.POST}, produces = "application/json")
	public Map<String, Object> getEventContent(@RequestBody String id) {
		return panoramaAnalysisService.getEventDetail(id);
	}
}
