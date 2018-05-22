package analyse.constants;

public final class Constants {
	
	public static final String displayName = "displayName";
	public static final String statistics = "statistics";

	public enum TimeGranularity{
		DAY("日", "yyyy-MM-dd"), WEEK("周", "yyyy-MM-dd"), MONTH("月", "yyyy-MM"), YEAR("年", "yyyy");
		
		TimeGranularity(String name, String format){
			this.name = name;
			this.format = format;
		}
		
		private String name;
		
		private String format;
		
		public String getName() {
			return name;
		}
		
		public String getFormat() {
			return format;
		}
	}
	
	/**
	 * 需要进行筛选的维度
	 * @author LU
	 *
	 */
	public enum MainDimension{
		TIME("时间", "eventTime"), TYPE("类型", "eventType"), AREA("地区", "location"), GOODSTYPE("涉及品类", "entities.涉及品类"),
		REASON("原因", "entities.原因");
		
		MainDimension(String name, String path){
			this.name = name;
			this.path = path;
		}
		
		private String name;
		
		private String path;
		
		public String getName() {
			return name;
		}
		
		public String getPath() {
			return path;
		}
	}
	
	public enum OtherDimension{
		ORIGIN("原产国", "entities.原产国"), PRODUCER("涉及生产商", "entities.涉及生产商"), BRAND("涉及品牌", "entities.涉及品牌"), RISK("风险", "entities.风险");
		
		OtherDimension(String name, String path){
			this.name = name;
			this.path = path;
		}
		
		private String name;
		
		private String path;
		
		public String getName() {
			return name;
		}
		
		public String getPath() {
			return path;
		}
	}
	
	public enum SortWay{
		ORDERED("升序"), REVERSED("降序");
		
		SortWay(String name){
			this.name = name;
		}
		
		private String name;
		
		private static final String path = "eventTime";
		
		public String getName() {
			return name;
		}
		
		public String getPath() {
			return path;
		}
		
	}
	
}
