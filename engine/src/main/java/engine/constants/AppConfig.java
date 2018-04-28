package engine.constants;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration("appConfig")
@ImportResource(locations = { "classpath:spring/applicationContext.xml" })
public class AppConfig {
	
}
