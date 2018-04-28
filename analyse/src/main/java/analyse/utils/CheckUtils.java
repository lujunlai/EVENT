package analyse.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import analyse.constants.Constants.MainDimension;
import analyse.constants.Constants.SortWay;
import analyse.constants.Constants.TimeGranularity;
import analyse.item.AnalysisFilter;
import analyse.item.AnalysisItem;
import analyse.item.AnalysisTime;

public class CheckUtils {

	/**
	 * 对输入进行校验
	 * @param analysisItem
	 */
	public static void islegalAnalysisItem(AnalysisItem analysisItem) {
		if (analysisItem.getAnalysisFilter() == null)
			analysisItem.setAnalysisFilter(new AnalysisFilter());
		if (analysisItem.getMainDimension() != MainDimension.TIME && analysisItem.getOrder() == null)
			analysisItem.setOrder(SortWay.REVERSED);
	}
	
	/**
	 * 获得初始化analysisItem
	 * @return
	 */
	public static AnalysisItem getInitItem() {
		SimpleDateFormat sdf = new SimpleDateFormat(TimeGranularity.MONTH.getFormat());
		
		AnalysisItem analysisItem = new AnalysisItem();
		analysisItem.setOrder(SortWay.REVERSED);
		
		AnalysisFilter analysisFilter = new AnalysisFilter();
		analysisFilter.setTimeGranularity(TimeGranularity.MONTH);
		
		AnalysisTime analysisTime = new AnalysisTime();
		
		Date nowDate = new Date();
		analysisTime.setEndTime(sdf.format(nowDate));
		Calendar calendar = new GregorianCalendar(); 
	    calendar.setTime(nowDate);
	    calendar.add(Calendar.YEAR, -1);
	    analysisTime.setStartTime(sdf.format(calendar.getTime()));
	    
		analysisFilter.setTime(Arrays.asList(analysisTime));
		
		analysisItem.setAnalysisFilter(analysisFilter);
		
		return analysisItem;
	}
	
}
