package engine.dao.mysql.item;

import java.io.Serializable;

public class ConfigItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1140192136786655927L;
	
	private Long id;
	
    private String mongodbHost;

    private String mongodbPort;

    private String mongodbUsername;

    private String mongodbPassword;

    private String mongodbDbname;
	
	private String esClusterHosts;
	
	private String esClusterName;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMongodbHost() {
		return mongodbHost;
	}

	public void setMongodbHost(String mongodbHost) {
		this.mongodbHost = mongodbHost;
	}

	public String getMongodbPort() {
		return mongodbPort;
	}

	public void setMongodbPort(String mongodbPort) {
		this.mongodbPort = mongodbPort;
	}

	public String getMongodbUsername() {
		return mongodbUsername;
	}

	public void setMongodbUsername(String mongodbUsername) {
		this.mongodbUsername = mongodbUsername;
	}

	public String getMongodbPassword() {
		return mongodbPassword;
	}

	public void setMongodbPassword(String mongodbPassword) {
		this.mongodbPassword = mongodbPassword;
	}

	public String getMongodbDbname() {
		return mongodbDbname;
	}

	public void setMongodbDbname(String mongodbDbname) {
		this.mongodbDbname = mongodbDbname;
	}

	public String getEsClusterHosts() {
		return esClusterHosts;
	}

	public void setEsClusterHosts(String esClusterHosts) {
		this.esClusterHosts = esClusterHosts;
	}

	public String getEsClusterName() {
		return esClusterName;
	}

	public void setEsClusterName(String esClusterName) {
		this.esClusterName = esClusterName;
	}

}
