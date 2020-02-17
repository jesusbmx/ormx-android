package ormx;

import java.io.Closeable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * @author Jesus
 */
public abstract class OrmDataBase extends SQLiteOpenHelper implements Closeable {
  
  private final LinkedList<QueryBuilder> querys = new LinkedList<QueryBuilder>();
  private final HashMap<Class<?>, OrmDao<?>> daos = new HashMap<Class<?>, OrmDao<?>>();
  
  private boolean debug;
  
// Costructor
  
	public OrmDataBase(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	@SuppressLint("NewApi")
	public OrmDataBase(Context context, String name, CursorFactory factory, int version,
            DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}

// Funciones  
   
  /**
   * Ejecuta consultas a la base de datos.
   *
   * @param sql query a ejecutar
   * @param params [opcional] parametros del query
   *
   * @return ResultSet con el resultado obtenido
   *
   * @throws SQLException
   */
  public OrmResult query(String sql, Object... params) {
    Cursor cursor = getReadableDatabase().rawQuery(sql, OrmUtils.args(params));
    return new OrmResult(cursor);
  }

  /**
   * Ejecuta sentencias a la base de datos.
   *
   * @param sql sentencia a ejecutar
   * @param params [opcional] parametros del query
   *
   * @return @true resultado obtenido
   *
   * @throws SQLException
   */
  public boolean execute(String sql, Object... params) {
    return executeUpdate(sql, params) == 1;
  }

  
  /**
   * Ejecuta sentencias insert y obtiene el id del registro insertado.
   * 
   * @param sql sentencia insert
   * @param params [opcional] parametros de la sentencia
   * 
   * @return el ID de la fila recién insertada, o -1 si se produjo un error
   * 
   * @throws SQLException 
   */
  public long executeInsert(String sql, Object... params) {
	SQLiteStatement stmt = null;
    try {
      stmt = getWritableDatabase().compileStatement(sql);
      OrmUtils.bindArgs(stmt, params);
    	  
      if (debug)
        OrmUtils.debugStmt(OrmDataBase.class, stmt, sql, params);
    	  
      return stmt.executeInsert();
    } finally {
      OrmUtils.close(stmt);
    }
  }
 
  @SuppressLint("NewApi")
  public int executeUpdate(String sql, Object... params) {
	SQLiteStatement stmt = null;
	try {
	  stmt = getWritableDatabase().compileStatement(sql);
	  OrmUtils.bindArgs(stmt, params);
	    	  
	  if (debug)
	    OrmUtils.debugStmt(OrmDataBase.class, stmt, sql, params);
	    	  
	    return stmt.executeUpdateDelete();
	 } finally {
	   OrmUtils.close(stmt);
	 }
  }
    
  /**
   * Inserta un registro en la base de datos.
   *
   * @param tabla donde se va a insertar la fila
   * @param datos mapa contiene los valores de columna iniciales para la fila.
   *      Las claves deben ser los nombres de las columnas 
   *      y los valores valores de la columna
   *
   * @return el ID de la fila recién insertada, o -1 si se produjo un error
   *
   * @throws SQLException
   */
  public long insert(String tabla, Map<String, Object> datos) {
    return queryBuilder().from(tabla).insert(datos);
  }

  public <V> boolean insert(V obj) {
    Class<V> classOf = (Class<V>) obj.getClass();
    return dao(classOf).insert(obj);
  }
  
  /**
   * Actualiza una registro en la base de datos.
   *
   * @param tabla donde se va a actualizar la fila.
   * @param datos mapa contiene los valores de columna iniciales para la fila. 
   *      Las claves deben ser los nombres de las columnas y los valores valores 
   *      de la columna.
   * @param whereClause [opcional] cláusula WHERE para aplicar al actualizar.
   *      Pasar null actualizará todas las filas.
   * @param whereArgs [opcional] Puede incluirse en la cláusula WHERE, que
   *      será reemplazado por los valores de whereArgs. Los valores
   *      se enlazará como cadenas.
   *
   * @return el número de filas afectadas.
   *
   * @throws SQLException
   */
  public int update(String tabla, Map<String, Object> datos, String whereClause, Object... whereArgs) {
     return queryBuilder().from(tabla).whereRaw(whereClause, whereArgs).update(datos);
  }
  
  public <V> int update(V obj) {
    Class<V> classOf = (Class<V>) obj.getClass();
    return dao(classOf).update(obj);
  }
  
  /**
   * Elimina un registro de la base de datos.
   * 
   * @param tabla donde se eliminara
   * @param whereClause [opcional] cláusula WHERE para aplicar la eliminación.
   *      Pasar null elimina todas las filas.
   * @param whereArgs [opcional] Puede incluirse en la cláusula WHERE, que
   *      será reemplazado por los valores de whereArgs. Los valores
   *      se enlazará como cadenas.
   * 
   * @return el número de filas afectadas.
   * 
   * @throws SQLException 
   */
  public int delete(String tabla, String whereClause, Object... whereArgs) {
    return queryBuilder().from(tabla).whereRaw(whereClause, whereArgs).delete();
  }
  
  public <V> int delete(V obj) {
    Class<V> classOf = (Class<V>) obj.getClass();
    return dao(classOf).delete(obj);
  }
  
  /**
   * Obtiene el numero de filas.
   *
   * @param tabla donde se buscaran las existencias
   * @param selection campo ha seleccionar
   * @param whereClause condicion
   * @param whereArgs [opcional] parametros del whereClause
   *
   * @return numero de existencia
   *
   * @throws SQLException
   */
  public long count(String tabla, String selection, String whereClause, Object... whereArgs) {
    return queryBuilder().from(tabla).whereRaw(whereClause, whereArgs)
            .get_select_count(selection);
  }
  
  @Override protected void finalize() throws Throwable {
    try {
      close();
    } finally {
      super.finalize();
    }
  }
  
  public synchronized QueryBuilder queryBuilder() {
    QueryBuilder query = querys.poll();
    if (query == null) query = new QueryBuilder(this);
    return query;
  }

  public synchronized void recycler(QueryBuilder query) {
    querys.add(query.reset());
  }
  
  public synchronized <V> OrmDao<V> dao(Class<V> classOf) {
    OrmDao<V> dao = (OrmDao<V>) daos.get(classOf);
    if (dao == null) {
      dao = new OrmDao.Builder<V>(this)
              .setClassOf(classOf)
              .build();
      
      putCache(classOf, dao);
    }
    return dao; 
  }
  
  public synchronized <V> void putCache(Class<V> classOf, OrmDao<V> dao) {
    daos.put(classOf, dao);
  }
  
  public boolean isDebug() {
    return debug;
  }

  public OrmDataBase setDebug(boolean debug) {
    this.debug = debug;
    return this;
  }
}
