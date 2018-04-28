package engine.filter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;

import engine.route.SelectDataSource;

@Component
public class AccessFilter extends ZuulFilter {

	@Autowired
	SelectDataSource selectDataSource;
	
    private static Logger logger     = LoggerFactory.getLogger(AccessFilter.class);

    private String[]      acceptUrls = {};
    private String[]      deniedUrls = {};

    public String[] getAcceptUrls() {
        return acceptUrls;
    }

    public void setAcceptUrls(String[] acceptUrls) {
        this.acceptUrls = acceptUrls;
    }

    public String[] getDeniedUrls() {
        return deniedUrls;
    }

    public void setDeniedUrls(String[] deniedUrls) {
        this.deniedUrls = deniedUrls;
    }

	@Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        logger.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
        
        if (StringUtils.isEmpty(request.getParameter("config_id"))) {
        	logger.warn("config id is empty");
        	ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            return null;
        }

        Long configId = new Long(request.getParameter("config_id"));
        try {
        	InputStream in = ctx.getRequest().getInputStream(); 
            String body = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
            JSONObject json;
            if (StringUtils.isEmpty(body))
            	json = new JSONObject();
            else
            	json = JSONObject.parseObject(body); 
            json.put("configId", configId);
            json.put("elasticsearchHelper", selectDataSource.getElasticsearchHelper(configId));
            json.put("mongodbHelper", selectDataSource.getMongodbHelper(configId));
            
            String jsonString = json.toString();
            
            final byte[] reqBodyBytes = jsonString.getBytes(Charset.forName("UTF-8"));
            
            ctx.setRequest(new HttpServletRequestWrapper(request){
            	 @Override
                 public ServletInputStream getInputStream() throws IOException {
                 	return new ServletInputStreamWrapper(reqBodyBytes); 
                 }
            	 @Override
                 public int getContentLength() { 
                  return reqBodyBytes.length; 
                 }
            	 @Override
                 public long getContentLengthLong() { 
                  return reqBodyBytes.length; 
                 }
            });
            return null;
        } 
        catch (IOException e) {
        	logger.error("make new request error", e);
        	ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            return null;
        }

//        if (acceptUrls.length != 0) {
//            for (String url : acceptUrls) {// 这些url不需要token
//                if (request.getRequestURI().contains(url)) {
//                    logger.info("access accept!");
//                    return null;
//                }
//            }
//        }
        
//        Object accessToken = request.getParameter("accessToken");// 暂时未启用token机制
//        if (accessToken == null) {
//            logger.warn("access token is empty");
//            ctx.setSendZuulResponse(false);
//            ctx.setResponseStatusCode(401);
//            return null;
//        }

//        if (deniedUrls.length != 0) {// 这些url有token也不让访问
//            for (String url : deniedUrls) {
//                if (request.getRequestURI().contains(url)) {
//                    logger.warn("access denied!");
//                    ctx.setSendZuulResponse(false);
//                    ctx.setResponseStatusCode(401);
//                    return null;
//                }
//            }
//        }
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

}
