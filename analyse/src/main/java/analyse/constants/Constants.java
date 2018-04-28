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
		TIME("时间", "eventTime"), TYPE("类型", "eventType"), AREA("地区", "location"), GOODSTYPE("商品类型", "");
		
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
		ORIGIN("产地", ""), PRODUCER("生产商", ""), BRAND("品牌", ""), RISK("风险", "");
		
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
