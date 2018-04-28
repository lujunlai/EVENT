package analyse;

import javax.servlet.Filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import pool.Pool;

@EnableEurekaClient
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class}) 
@ComponentScan("analyse")
public class Analyse {
	
	public static void main(String[] args) {
	    	SpringApplication.run(Analyse.class, args);
	    }
	
	 /**
	   * 配置过滤器
	   * @return
	   */
	  @Bean
	  public FilterRegistrationBean someFilterRegistration() {
	      FilterRegistrationBean registration = new FilterRegistrationBean();
	      registration.setFilter(sessionFilter());
	      registration.addUrlPatterns("/*");
	      registration.addInitParameter("paramName", "paramValue");
	      registration.setName("sessionFilter");
	      return registration;
	  }
	  
	  /**
	   * 创建一个bean
	   * @return
	   */
	  @Bean(name = "sessionFilter")
	  public Filter sessionFilter() {
	      return new Pool();
	  }
}
