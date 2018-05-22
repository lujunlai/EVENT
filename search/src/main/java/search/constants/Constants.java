package search.constants;

public class Constants {
	
	public enum Dimension{
		PRODUCT("产品", ""), BRAND("品牌", ""), ROLES("", ""), TITLE("标题", ""), CONTENT("内容", "");
		
		Dimension(String name, String path){
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

}
