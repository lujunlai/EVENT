package mysql;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.support.SqlSessionDaoSupport;

public class DaoHelper extends SqlSessionDaoSupport {

    public SqlSession getSqlSession() {
        return super.getSqlSession();
    }

    public int insert(String nameSpace, String sqlID, Object paramObject) {
        return super.getSqlSession().insert(getStatement(nameSpace, sqlID), paramObject);
    }

    public int insert(String nameSpace, Object paramObject) {
        return insert(nameSpace, "insert", paramObject);
    }

    public int update(String nameSpace, String sqlID, Object paramObject) {
        return super.getSqlSession().update(getStatement(nameSpace, sqlID), paramObject);
    }

    public int update(String nameSpace, Object paramObject) {
        return update(nameSpace, "update", paramObject);
    }

    public int delete(String nameSpace, String sqlID, Object paramObject) {
        return super.getSqlSession().delete(getStatement(nameSpace, sqlID), paramObject);
    }

    public int delete(String nameSpace, Object paramObject) {
        return delete(nameSpace, "delete", paramObject);
    }

    public <T extends Object> T getOne(String nameSpace, String sqlID, Object paramObject) {
        return super.getSqlSession().selectOne(getStatement(nameSpace, sqlID), paramObject);
    }

    public <T extends Object> T getOne(String nameSpace, Object paramObject) {
        return getOne(nameSpace, "getOne", paramObject);
    }

    public <T extends Object> List<T> query(String nameSpace, String sqlID, Object paramObject) {
        return super.getSqlSession().selectList(getStatement(nameSpace, sqlID), paramObject);
    }

    public <T extends Object> List<T> query(String nameSpace, Object paramObject) {
        return query(nameSpace, "query", paramObject);
    }

    public <T extends Object> List<T> page(String nameSpace, String sqlID, Object paramObject) {
        return super.getSqlSession().selectList(getStatement(nameSpace, sqlID), paramObject);
    }

    public <T extends Object> List<T> page(String nameSpace, Object paramObject) {
        return page(nameSpace, "page", paramObject);
    }

    public int count(String nameSpace, String sqlID, Object paramObject) {
    	Object o = super.getSqlSession().selectOne(getStatement(nameSpace, sqlID), paramObject);
        return Integer.valueOf(String.valueOf(o));
    }

    public int count(String nameSpace, Object paramObject) {
        return count(nameSpace, "count", paramObject);
    }

    private String getStatement(String nameSpace, String sqlID) {
        return nameSpace + "." + sqlID;
    }
}