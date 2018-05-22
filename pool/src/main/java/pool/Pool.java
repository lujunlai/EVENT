package pool;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StreamUtils;

import com.alibaba.fastjson.JSONObject;

import elasticsearch.ElasticsearchHelper;
import mongodb.MongodbHelper;

public class Pool implements Filter {
	
	private static ConcurrentMap<Long, ElasticsearchHelper> esPool = new ConcurrentHashMap<Long, ElasticsearchHelper>();
	
	private static ConcurrentMap<Long, MongodbHelper> mongodbPool = new ConcurrentHashMap<Long, MongodbHelper>();
	
	private static ThreadLocal<ElasticsearchHelper> elasticsearchHelper = new ThreadLocal<ElasticsearchHelper>();
	
	private static ThreadLocal<MongodbHelper> mongodbHelper = new ThreadLocal<MongodbHelper>();
	
	public static ElasticsearchHelper getElasticsearchHelper() {
		return elasticsearchHelper.get();
	}
	
	public static MongodbHelper getMongodbHelper() {
		return mongodbHelper.get();
	}
	
	private void putElasticsearchHelper(Long configId, ElasticsearchHelper elasticsearchHelper) throws UnknownHostException {
		if (esPool.get(configId) == null || esPool.get(configId).getClient() == null) {
			System.out.println("init new elasticsearchHelper, configId: " + configId);
			elasticsearchHelper.afterPropertiesSet();
			esPool.put(configId, elasticsearchHelper);
		}
		Pool.elasticsearchHelper.set(esPool.get(configId));
	}
	
	private void putMongodbHelper(Long configId, MongodbHelper mongodbHelper) {
		if (mongodbPool.get(configId) == null || mongodbPool.get(configId).getMongoClient() == null) {
			System.out.println("init new mongodbHelper, configId: " + configId);
			mongodbHelper.afterPropertiesSet();
			mongodbPool.put(configId, mongodbHelper);
		}
		Pool.mongodbHelper.set(mongodbPool.get(configId));
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = new BufferedServletRequestWrapper((HttpServletRequest)request);
		InputStream in = httpRequest.getInputStream();
		String body = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
		JSONObject json = JSONObject.parseObject(body);
		ElasticsearchHelper elasticsearchHelper = JSONObject.parseObject(json.getString("elasticsearchHelper"), ElasticsearchHelper.class);
		MongodbHelper mongodbHelper = JSONObject.parseObject(json.getString("mongodbHelper"), MongodbHelper.class);
		Long configId = json.getLong("configId");
		putElasticsearchHelper(configId, elasticsearchHelper);
		putMongodbHelper(configId, mongodbHelper);
		chain.doFilter(httpRequest, response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}

