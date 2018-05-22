package mongodb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class MongodbHelper implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4566593827781045169L;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private MongoClient mongoClient = null;
	
    private String hostName;
    
    private Integer hostPort;
    
    private String userName;
    
    private String dbName;
    
    private String password;
    
    private Integer connectionsPerHost = 30;
    
    private Integer threadsAllowedToBlockForConnectionMultiplier = 30;
    
    private Integer maxWaitTime = 60000;
    
    private Integer connectTimeout = 60000;
    
    public MongoDatabase getDataBase(String dbName){
    	return mongoClient.getDatabase(dbName);
    }
    
    public <TDocument> MongoCollection<TDocument> getCollection(String dbName, String collectionName, Class<TDocument> documentClass){
    	return getDataBase(dbName).getCollection(collectionName, documentClass);
    }
    
    public MongoCollection<Document> getCollection(String dbName, String collectionName){
    	return getDataBase(dbName).getCollection(collectionName);
    }
    
    public void insertOne(String dbName, String collectionName, Document doc){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	collection.insertOne(doc);
    }
    
    public String insertOneReturnId(String dbName, String collectionName, Document doc){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	collection.insertOne(doc);
    	return doc.get("_id").toString();
    }
    
    public void insertMany(String dbName, String collectionName, List<Document> documents){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	
    	collection.insertMany(documents);
    }
    
    public void insertMany(String dbName, String collectionName, List<Document> documents, InsertManyOptions options){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	
    	collection.insertMany(documents, options);
    }

    public UpdateResult replaceOne(String dbName, String collectionName, Bson filter, Document document, boolean upsert) {
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if (collection == null) {
			throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
		}

		return collection.replaceOne(filter, document, new UpdateOptions().upsert(upsert));
	}

    public UpdateResult updateOne(String dbName, String collectionName, Bson filter, Bson update, boolean upsert){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if (collection == null) {
			throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
		}
    	return collection.updateOne(filter, update, new UpdateOptions().upsert(upsert));
    }
    
	/**
	 *
	 * @param dbName name of database
	 * @param collectionName name of collection
	 * @param limit the limit number of result, 0 for Unlimited
	 * @return
	 */
	public FindIterable<Document> find(String dbName, String collectionName, Integer limit){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	return collection.find().limit(limit);
    }

	/**
	 *
	 * @param dbName
	 * @param collectionName
	 * @param filter
	 * @param limit the limit number of result, 0 for Unlimited
	 * @return
	 */
    public FindIterable<Document> find(String dbName, String collectionName, Bson filter, Integer limit){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	return collection.find(filter).limit(limit);
    }
    
    /**
     * 排序获取
     * @param dbName
     * @param collectionName
     * @param sort
     * @param limit
     * @return
     */
    public FindIterable<Document> findBySort(String dbName, String collectionName, Bson sort, Integer limit){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	return collection.find().limit(limit).sort(sort);
    }
    
    /**
     * 获取分页结果列表
     * @param dbName
     * @param collectionName
     * @param sort
     * @param from
     * @param pageSize
     * @return
     */
    public FindIterable<Document> findByPage(String dbName, String collectionName, Bson sort, Integer from,Integer pageSize){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	return collection.find().sort(sort).skip((from - 1) * pageSize).limit(pageSize);
    }
    
    /**
     * 获取聚合的数量
     * @param dbName
     * @param collectionName
     * @param aggregateType
     * @return
     */
    public Map<String,Integer> getAggCount(String dbName, String collectionName,String aggregateType, Integer limit){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	List<Document> pipeline = Arrays.asList(new Document("$group",
				new Document("_id", aggregateType).append("count", new Document("$sum", 1))),new Document("$sort", new Document("count", -1)),new Document("$limit", limit));
    	AggregateIterable<Document> aggregateIterable = collection.aggregate(pipeline);
    	MongoCursor<Document> iterator = aggregateIterable.iterator();
    	Integer count= null;
    	Map<String,Integer> map = new HashMap<String,Integer>();
    	while (iterator.hasNext()) {
			Document document = (Document) iterator.next();
			count = document.getInteger("count");
			@SuppressWarnings("unchecked")
			ArrayList<String> list = (ArrayList<String>) document.get("_id");
			String name = null;
			if(list.size()!=0) {
				name = list.get(0);
				if(name!=null) {
					map.put(name, count);
				}
			}

		}
    	return map;
    }

	/**
	 *
	 * @param dbName
	 * @param collectionName
	 * @param resultClass
	 * @param limit the limit number of result, 0 for Unlimited
	 * @param <TResult>
	 * @return
	 */
    public <TResult> FindIterable<TResult> find(String dbName, String collectionName, Class<TResult> resultClass, Integer limit){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	return collection.find(resultClass).limit(limit);
    }

	/**
	 *
	 * @param dbName
	 * @param collectionName
	 * @param filter
	 * @param resultClass
	 * @param limit the limit number of result, 0 for Unlimited
	 * @param <TResult>
	 * @return
	 */
    public <TResult> FindIterable<TResult> find(String dbName, String collectionName, Bson filter, Class<TResult> resultClass, Integer limit){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	return collection.find(filter, resultClass).limit(limit);
    }
    
    public long count(String dbName, String collectionName){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	return collection.count();
    }
    
    public long count(String dbName, String collectionName, Bson filter){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	return collection.count(filter);
    }
    
    public long count(String dbName, String collectionName, Bson filter, CountOptions options){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	return collection.count(filter, options);
    }
    
    public DeleteResult deleteOne(String dbName, String collectionName, Bson filter){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	return collection.deleteOne(filter);
    }
    
    public DeleteResult deleteMany(String dbName, String collectionName, Bson filter){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	return collection.deleteMany(filter);
    }
    
    public String createIndex(String dbName, String collectionName, Bson keys){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	return collection.createIndex(keys);
    }
    
    public String createIndex(String dbName, String collectionName, Bson keys, IndexOptions options){
    	MongoCollection<Document> collection = getCollection(dbName, collectionName);
    	if(collection == null){
    		throw new RuntimeException("no such collection[" + collectionName + "] in db[" + dbName + "]");
    	}
    	return collection.createIndex(keys, options);
    }
    
	public void afterPropertiesSet() {
		MongoClientOptions.Builder build = new MongoClientOptions.Builder();
        build.connectionsPerHost(connectionsPerHost);   //与目标数据库能够建立的最大connection数量为30
        build.threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier); //如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
        /*
         * 一个线程访问数据库的时候，在成功获取到一个可用数据库连接之前的最长等待时间为2分钟
         * 这里比较危险，如果超过maxWaitTime都没有获取到这个连接的话，该线程就会抛出Exception
         * 故这里设置的maxWaitTime应该足够大，以免由于排队线程过多造成的数据库访问失败
         */
        build.maxWaitTime(maxWaitTime);
        build.connectTimeout(connectTimeout);    //与数据库建立连接的timeout设置为1分钟

        MongoClientOptions myOptions = build.build();
        try {
			//数据库连接实例
			ServerAddress serverAddress = new ServerAddress(hostName, hostPort);
			if (userName != null & dbName != null & password != null) {
				List<MongoCredential> lstCredentials = Arrays.asList(MongoCredential.createMongoCRCredential(userName, dbName, password.toCharArray()));
				mongoClient = new MongoClient(serverAddress, lstCredentials, myOptions);
			}
			else {
				mongoClient = new MongoClient(serverAddress, myOptions);
			}
        }catch (MongoException e){
            logger.error(e.getMessage(), e);
        }
	}

	public MongoClient getMongoClient() {
		return mongoClient;
	}


	public void setMongoClient(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public Integer getHostPort() {
		return hostPort;
	}

	public void setHostPort(Integer hostPort) {
		this.hostPort = hostPort;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getConnectionsPerHost() {
		return connectionsPerHost;
	}

	public void setConnectionsPerHost(Integer connectionsPerHost) {
		this.connectionsPerHost = connectionsPerHost;
	}

	public Integer getThreadsAllowedToBlockForConnectionMultiplier() {
		return threadsAllowedToBlockForConnectionMultiplier;
	}

	public void setThreadsAllowedToBlockForConnectionMultiplier(
			Integer threadsAllowedToBlockForConnectionMultiplier) {
		this.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
	}

	public Integer getMaxWaitTime() {
		return maxWaitTime;
	}

	public void setMaxWaitTime(Integer maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}

	public Integer getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	
}

