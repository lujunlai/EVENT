package analyse.item;

import java.io.Serializable;

public class AnalysisTime implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8243222896830430802L;

	private String startTime;
	
	private String endTime;

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
    
	@Override
	public String toString() {
		return String.format(
				"{"
				+ "startTime=%s,"
				+ "endTime=%s"
				+ "}",
				startTime, endTime);
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof AnalysisTime) {
			AnalysisTime analysisTime = (AnalysisTime)object;
            if(
            		(startTime == analysisTime.getStartTime() || startTime != null && startTime.equals(analysisTime.getStartTime())) &&
            		(endTime == analysisTime.getEndTime() || endTime != null && endTime.equals(analysisTime.getEndTime()))
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
		if (startTime != null)
			result = 37 * result + startTime.hashCode();
		if (endTime != null)
			result = 37 * result + endTime.hashCode();
		return result;
	}
}
