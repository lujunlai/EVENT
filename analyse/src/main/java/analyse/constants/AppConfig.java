package analyse.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration("appConfig")
@ImportResource(locations = { "classpath:spring/applicationContext.xml" })
public class AppConfig {
	
	@Value("${es.index.event}")
	private String esIndexEvent;
	
	@Value("${es.type.event}")
	private String esTypeEvent;

	public String getEsIndexEvent() {
		return esIndexEvent;
	}
	
	public void setEsIndexEvent(String esIndexEvent) {
		this.esIndexEvent = esIndexEvent;
	}
	
	public String getEsTypeEvent() {
		return esTypeEvent;
	}
	
	public void setEsTypeEvent(String esTypeEvent) {
		this.esTypeEvent = esTypeEvent;
	}
	
}

