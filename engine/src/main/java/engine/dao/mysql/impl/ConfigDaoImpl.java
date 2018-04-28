package engine.dao.mysql.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import engine.dao.mysql.ConfigDao;
import engine.dao.mysql.item.ConfigItem;
import mysql.DaoHelper;

@Repository
public class ConfigDaoImpl implements ConfigDao {
	
	@Autowired
	DaoHelper mysqlDaoHelper;
	
	public static final String NAMESPACE = "domain_config";

	@Override
	public ConfigItem selectById(Long id) {
		return mysqlDaoHelper.getOne(NAMESPACE, id);
	}

}
