package ormx;

import android.content.ContentValues;
import android.database.Cursor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import ormx.annot.ColumnInfo;


public class OrmField<T> implements CharSequence {

  public final Field field;
  public final ColumnInfo info;
  public final String name;
  public final OrmType<T> type;
  
  final Method get, set;

  private OrmField(Field field, Method get, Method set, ColumnInfo info) throws Exception {
    this.field = field;
    this.get = get;
    this.set = set;
    this.info = info;
    this.name = OrmUtils.columnName(info, field);
    this.type = OrmUtils.type(info, field);
  }

  public T get(Object object) throws Exception {
    if (get != null) 
      return (T) get.invoke(object);
    else
      return (T) field.get(object);
  }
  
  public void toValues(ContentValues values, Object object) throws Exception {
    type.toValues(values, name, get(object));
  }

  public void set(Object object, T value) throws Exception {
    if (set != null) 
      set.invoke(object, value);
    else
      field.set(object, value);
  }
  
  public void set(Object obj, Cursor rs, int index) throws Exception {
    set(obj, type.fromResult(rs, name, index));
  }
  
  @Override public int length() {
    return name.length();
  }
  @Override public char charAt(int index) {
    return name.charAt(index);
  }
  @Override public CharSequence subSequence(int start, int end) {
    return name.subSequence(start, end);
  }
  @Override public String toString() {
    return name;
  }
  
  /* package */ static OrmField[] fields(Class<?> classOf) throws Exception {
    final Field[] fs = classOf.getDeclaredFields();
    final Method[] m = classOf.getDeclaredMethods();
    final OrmField[] result = new OrmField[fs.length];
    
    ColumnInfo info;
    Method get;
    Method set;
    int y = 0;
    
    for (Field f : fs) {
      info = f.getAnnotation(ColumnInfo.class);
      if (info != null) {
        get = OrmUtils.getMethod(f, m);
        set = OrmUtils.setMethod(f, m);
        result[y++] = new OrmField(f, get, set, info);
      }
    }
    
    final OrmField[] copy = new OrmField[y];
    System.arraycopy(result, 0, copy, 0, y);
    return copy;
  }
  
}
