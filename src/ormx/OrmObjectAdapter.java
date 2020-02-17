package ormx;

import android.content.ContentValues;
import android.database.Cursor;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OrmObjectAdapter<T> {
  private static final HashMap<Class<?>, OrmObjectAdapter<?>> cache = 
          new HashMap<Class<?>, OrmObjectAdapter<?>>();
  
  final Class<T> classOf;
  final Constructor<T> constructor;
  final OrmField fields[];
  
  private OrmObjectAdapter(Class<T> classOf) throws Exception {
    this.classOf = classOf;
    this.constructor = classOf.getDeclaredConstructor();
    this.fields = OrmField.fields(classOf);
  }
  
  public static <V> OrmObjectAdapter<V> of(Class<V> classOf) throws RuntimeException {
    try {
      OrmObjectAdapter<V> pojox = (OrmObjectAdapter<V>) cache.get(classOf);
      if (pojox == null) {
        pojox = new OrmObjectAdapter<V>(classOf);
        cache.put(classOf, pojox);
      }
      return pojox;
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public T newInstance() throws Exception {
    return constructor.newInstance();
  }

  public void fill(T src, ContentValues dest) throws Exception {
    for (int i = 0; i < fields.length; i++) {
      fields[i].toValues(dest, src);
    }
  }
  
  public void fill(Cursor src, T dest, int[]index) throws Exception {
    for (int i = 0; i < fields.length; i++) {
      fields[i].set(dest, src, index[i]);
    }
  }
  
  public void fill(Cursor src, List<T> dest) throws Exception {
  	final int[] index = indexs(src);
    while (src.moveToNext()) {
      dest.add(cursorToEntityOrThrow(src, index));
    }
  }
  
  public ContentValues values(T obj) {
    try {
      final ContentValues result = new ContentValues(fields.length);
      fill(obj, result);
      return result;
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
 
  public T cursorToEntityOrThrow(Cursor c, int[]index) throws Exception {
    T result = newInstance();
    fill(c, result, index);
    return result;
  }
  
  public T cursorToEntityOrThrow(Cursor c) throws Exception {
    return cursorToEntityOrThrow(c, indexs(c));
  }
  
  public T entity(Cursor c) throws RuntimeException {
    try {
      return c.moveToNext() ? cursorToEntityOrThrow(c) : null;
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  
  public List<T> list(Cursor c) throws RuntimeException {
    try {
      final List<T> result = new ArrayList<T>(c.getCount());
      fill(c, result);
      return result;
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    } 
  }
  
  public OrmIterator<T> iterator(Cursor rs) {
    return new OrmIterator<T>(this, rs);
  }
  
  public Class<T> getClassOf() {
    return classOf;
  }

  public OrmField[] fields() {
    return fields;
  }
  
  public int[] indexs(Cursor c) {
		int[] result = new int[fields.length];
 		for (int i = 0; i < result.length; i++) {
			result[i] = c.getColumnIndex(fields[i].name);
		}
		return result;
	}
  
  public <V> OrmField<V> fieldByName(CharSequence columnName) {
    if (OrmUtils.isEmpty(columnName)) 
      return null;

    int pos = Arrays.binarySearch(fields, columnName);
    if (pos > -1) {
      return fields[pos];
    }
    
    return null;
  }
}
