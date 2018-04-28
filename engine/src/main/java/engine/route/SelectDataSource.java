package engine.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import elasticsearch.ElasticsearchHelper;
import engine.dao.mysql.ConfigDao;
import engine.dao.mysql.item.ConfigItem;
import mongodb.MongodbHelper;

@Component
public class SelectDataSource {

	@Autowired
	ConfigDao configDao;
	
    public ElasticsearchHelper getElasticsearchHelper(Long configId) {
    	ConfigItem configItem = configDao.selectById(configId);
    	
    	ElasticsearchHelper elasticsearchHelper = new ElasticsearchHelper();
    	elasticsearchHelper.setHosts(configItem.getEsClusterHosts());
    	elasticsearchHelper.setClusterName(configItem.getEsClusterName());
    	
    	return elasticsearchHelper;
    }
	
    public MongodbHelper getMongodbHelper(Long configId) {
    	ConfigItem configItem = configDao.selectById(configId);
    	
    	MongodbHelper mongodbHelper = new MongodbHelper();
    	mongodbHelper.setDbName(configItem.getMongodbDbname());
    	mongodbHelper.setPassword(configItem.getMongodbPassword());
    	mongodbHelper.setHostName(configItem.getMongodbHost());
    	mongodbHelper.setHostPort(new Integer(configItem.getMongodbPort()));
    	mongodbHelper.setUserName(configItem.getMongodbUsername());
    	
    	return mongodbHelper;
    } 
    
}
