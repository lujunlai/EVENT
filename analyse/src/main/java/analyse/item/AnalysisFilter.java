package analyse.item;

import java.io.Serializable;
import java.util.List;

import analyse.constants.Constants.TimeGranularity;
import analyse.utils.Utils;

public class AnalysisFilter implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7439892757284455429L;
	
	private TimeGranularity timeGranularity;
	
	private List<String> type;
	
	private List<String> area;
	
	private List<String> goodsType;
	
	private List<AnalysisTime> time;

	public TimeGranularity getTimeGranularity() {
		return timeGranularity;
	}

	public void setTimeGranularity(TimeGranularity timeGranularity) {
		this.timeGranularity = timeGranularity;
	}

	public List<String> getType() {
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
	}

	public List<String> getArea() {
		return area;
	}

	public void setArea(List<String> area) {
		this.area = area;
	}

	public List<String> getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(List<String> goodsType) {
		this.goodsType = goodsType;
	}

	public List<AnalysisTime> getTime() {
		return time;
	}

	public void setTime(List<AnalysisTime> time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return String.format(
				"{"
				+ "timeGranularity=%s,"
				+ "type=%s,"
				+ "area=%s,"
				+ "goodsType=%s,"
				+ "time=%s"
				+ "}",
				timeGranularity, type, area, goodsType, time);
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof AnalysisFilter) {
			AnalysisFilter analysisFilter = (AnalysisFilter)object;
            if(
            		(timeGranularity == analysisFilter.getTimeGranularity() || timeGranularity != null && timeGranularity.equals(analysisFilter.getTimeGranularity())) &&
            		(Utils.listEqual(type, analysisFilter.getType())) &&
            		(Utils.listEqual(area, analysisFilter.getArea())) &&
            		(Utils.listEqual(goodsType, analysisFilter.getGoodsType())) &&
            		(Utils.listEqual(time, analysisFilter.getTime()))
            		) {
            	return true;
            } 
            else {
                return false;
            }
        } 
		else {
            return false;
        }
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		if (timeGranularity != null)
			result = 37 * result + timeGranularity.hashCode();
		if (type != null)
			result = 37 * result + type.hashCode();
		if (area != null)
			result = 37 * result + area.hashCode();
		if (goodsType != null)
			result = 37 * result + goodsType.hashCode();
		if (time != null)
			result = 37 * result + time.hashCode();
		return result;
	}
	
}
