package ormx;

import java.io.Closeable;
import java.util.List;

import android.content.ContentValues;

/**
 * @author jesus
 */
public class OrmDao<T> implements Closeable {
  final OrmDataBase db; 
  final OrmObjectAdapter<T> adapter;
  final String table;
  final String key;
  final boolean autoIncrement;
  final OrmField<Long> primaryKey;
  
  OrmDao(Builder<T> builder) {
    this.db = builder.db;
    this.adapter = builder.adapter;
    this.table = builder.table;
    this.key = builder.key;
    this.autoIncrement = builder.autoIncrement;
    this.primaryKey = builder.primaryKey;
  }

  public OrmDataBase db() {
    return db;
  }
  
  public OrmObjectAdapter<T> adapter() {
    return adapter;
  }
  
  public String table() {
    return table;
  }
  
  public QueryBuilder queryBuilder() {
    return db().queryBuilder().select(adapter.fields()).from(table);
  }

  public boolean setId(T obj, long id) {
    if (primaryKey == null) 
      return false;
    
    try {
      primaryKey.set(obj, id);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
  
  public long getId(T obj) {
    if (primaryKey == null) 
      return 0;
    
    try {
      return primaryKey.get(obj);
    } catch (Exception e) {
      return 0;
    }
  }
  
  public ContentValues vars(T object) {
    final ContentValues vars = adapter.values(object);
    if (autoIncrement) vars.remove(key);
    return vars;
  }

  public boolean insert(T obj)  {
    final ContentValues vars = vars(obj);
    
    long id = queryBuilder().insert(vars);
      
    if (autoIncrement) 
      setId(obj, id);
      
    return id != -1;
  }
  
  public int update(T obj) {
    final long id = getId(obj);
    final ContentValues vars = vars(obj);
    return queryBuilder().where(key, id).update(vars);
  }

  public boolean save(T obj) {
    final long id = getId(obj);
    
    if (idExists(id)) {
      return update(obj) > 0;
    } else {
      return insert(obj);
    }
  }
  
  public int delete(T obj)  {
    long id = getId(obj);
    return deleteById(id);
  }
  
  public int deleteById(long id)  {
    return queryBuilder().where(key, id).delete();
  }
  
  public int deleteAll()  {
   return queryBuilder().delete();
  }
  
  public int deleteIds(long... ids)  {
    return queryBuilder().where_in(key, ids).delete();
  }
  
  public long count()  {
    return queryBuilder().get_select_count(key);
  }
  
  public long count(String whereClause, Object... whereArgs)  {
    return db.count(table, key, whereClause, whereArgs);
  }
  
  public boolean idExists(long id)  {
    return queryBuilder().where(key, id).get_select_count(key) > 0;
  }
  
  public T findById(long id)  {
    return findByField(key, id);
  }
  
  public T findByField(String column, Object value)  {
    return queryBuilder().where(column, value).get().row(adapter);
  }
  
  public List<T> query(QueryBuilder query)  {
    return query.get().list(adapter);
  }
  
  public List<T> query(String sql, Object... params)  {
    return db.query(sql, params).list(adapter);
  }
  
  public List<T> queryForEq(String column, Object value)  {
    return queryBuilder().where(column, value).get().list(adapter);
  }
  
  public List<T> queryForAll()  {
    return queryBuilder().get().list(adapter);
  }
 
  public OrmIterator<T> iterator(QueryBuilder query)  {
    return query.get().iterator(adapter);
  }
  
  public OrmIterator<T> iterator(String sql, Object... params)  {
    return db.query(sql, params).iterator(adapter);
  }
  
  public OrmIterator<T> iterator()  {
    return queryBuilder().get().iterator(adapter);
  }
  
  @Override public void close() {
    db.close();
  }
  
  public static class Builder<R> 
  {
    final OrmDataBase db;
    Class<R> classOf;
    OrmObjectAdapter<R> adapter;
    String table;
    String key = "id";
    boolean autoIncrement;
    OrmField<Long> primaryKey;

    public Builder(OrmDataBase db) {
      this.db = db;
    }

    public Builder<R> setClassOf(Class<R> classOf) {
      this.classOf = classOf;
      return this;
    }
    
    public OrmDao<R> build() {
      adapter = OrmObjectAdapter.of(classOf);
      table = OrmUtils.tableName(classOf);
      primaryKey = OrmUtils.primaryKey(adapter);

      if (primaryKey != null) {
        key = primaryKey.name;
        autoIncrement = primaryKey.info.autoIncrement();
      } 

      return new OrmDao<R>(this);
    }
  }
}