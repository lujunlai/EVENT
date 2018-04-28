package engine.dao.mysql;

import engine.dao.mysql.item.ConfigItem;

public interface ConfigDao {

	ConfigItem selectById(Long id);
	
}
