package report.route;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

import elasticsearch.ElasticsearchHelper;

public class EsPool implements HandlerInterceptor {

	private static ConcurrentMap<Long, ElasticsearchHelper> esPool = new ConcurrentHashMap<Long, ElasticsearchHelper>();
	
	private static ThreadLocal<ElasticsearchHelper> elasticsearchHelper = new ThreadLocal<ElasticsearchHelper>();
	
	public static ElasticsearchHelper getElasticsearchHelper() {
		return elasticsearchHelper.get();
	}
	
	private void putElasticsearchHelper(Long configId, ElasticsearchHelper elasticsearchHelper) throws UnknownHostException {
		if (esPool.get(configId) == null || esPool.get(configId).getClient() == null) {
			elasticsearchHelper.afterPropertiesSet();
			esPool.put(configId, elasticsearchHelper);
		}
		EsPool.elasticsearchHelper.set(elasticsearchHelper);
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
		InputStream in = arg0.getInputStream();
		ObjectInputStream objInt = new ObjectInputStream(in);
        JSONObject json = (JSONObject)objInt.readObject() ; 
		ElasticsearchHelper elasticsearchHelper = (ElasticsearchHelper)json.get("elasticsearchHelper");
		Long configId = (Long)json.get("configId");
		putElasticsearchHelper(configId, elasticsearchHelper);
		return true;
	}
	
}
