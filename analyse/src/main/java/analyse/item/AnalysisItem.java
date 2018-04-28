package analyse.item;

import java.io.Serializable;

import analyse.constants.Constants.MainDimension;
import analyse.constants.Constants.SortWay;

public class AnalysisItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7377515506644690790L;

	private MainDimension mainDimension;
	
	private AnalysisFilter analysisFilter;
	
	private SortWay order;
	
	public MainDimension getMainDimension() {
		return mainDimension;
	}

	public void setMainDimension(MainDimension mainDimension) {
		this.mainDimension = mainDimension;
	}

	public AnalysisFilter getAnalysisFilter() {
		return analysisFilter;
	}

	public void setAnalysisFilter(AnalysisFilter analysisFilter) {
		this.analysisFilter = analysisFilter;
	}

	public SortWay getOrder() {
		return order;
	}

	public void setOrder(SortWay order) {
		this.order = order;
	}
	
	@Override
	public String toString() {
		return String.format(
				"{"
				+ "mainDimension=%s,"
				+ "analysisFilter=%s,"
				+ "order=%s"
				+ "}",
				mainDimension, analysisFilter, order);
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof AnalysisItem) {
			AnalysisItem analysisItem = (AnalysisItem)object;
            if(
            		(mainDimension == analysisItem.getMainDimension() || mainDimension != null && mainDimension.equals(analysisItem.getMainDimension())) &&
            		(analysisFilter == analysisItem.getAnalysisFilter() || analysisFilter != null && analysisFilter.equals(analysisItem.getAnalysisFilter())) &&
            		(order == analysisItem.getOrder() || order != null && order.equals(analysisItem.getOrder()))
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
		if (mainDimension != null)
			result = 37 * result + mainDimension.hashCode();
		if (analysisFilter != null)
			result = 37 * result + analysisFilter.hashCode();
		if (order != null)
			result = 37 * result + order.hashCode();
		return result;
	}
}
