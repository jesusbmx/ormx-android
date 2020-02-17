package ormx;

import android.content.ContentValues;
import android.database.Cursor;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;

public abstract class OrmType<T> {
  public static final HashMap<Class<?>, OrmType<?>> types = new HashMap<Class<?>, OrmType<?>>(17);
  public static final HashMap<Class<?>, OrmType<?>> others = new HashMap<Class<?>, OrmType<?>>();
 
  static {
    types.put(String.class, new StringType());

    BoolType boolAdapter = new BoolType();
    types.put(boolean.class, boolAdapter);
    types.put(Boolean.class, boolAdapter);

    ShortType shortAdapter = new ShortType();
    types.put(short.class, shortAdapter);
    types.put(Short.class, shortAdapter);

    IntType intAdapter = new IntType();
    types.put(int.class, intAdapter);
    //adapters.put(byte.class, intAdapter);
    types.put(Integer.class, intAdapter);

    LongType longAdapter = new LongType();
    types.put(long.class, longAdapter);
    types.put(Long.class, longAdapter);

    FloatType floatAdapter = new FloatType();
    types.put(float.class, floatAdapter);
    types.put(Float.class, floatAdapter);

    DoubleType doubleAdapter = new DoubleType();
    types.put(double.class, doubleAdapter);
    types.put(Double.class, doubleAdapter);

    types.put(byte[].class, new BytesType());

    types.put(Date.class, new DateType());
    
    types.put(Object.class, new ObjectType());
  }
  
  public abstract void toValues(ContentValues values, String column, T value);

  public abstract T fromResult(Cursor c, String column, int index);
  
  public static <V> OrmType<V> of(Class<?> type) {
	OrmType<V> typeAdapter = (OrmType<V>) types.get(type);
	//return typeAdapter == null ? DEFAULT_TYPE_ADAPTER : typeAdapter;
	if (typeAdapter == null) {
	   throw new RuntimeException(
	            String.format("No se ha definido un adaptador para: '%s'", type)
	    );
	  }
	return typeAdapter;
  }
  
  public static <V> void put(Class<V> type, OrmType<V> typeAdapter) {
    types.put(type, typeAdapter);
  }
  
  public static <V> OrmType<V> other(Class<?> type) throws Exception {
	OrmType<V> typeAdapter = (OrmType<V>) others.get(type);
	    
	if (typeAdapter == null) {
	   Constructor<OrmType<V>> constructor = 
	           (Constructor<OrmType<V>>) type.getDeclaredConstructor();
	      
	   typeAdapter = constructor.newInstance();
	   others.put(type, typeAdapter);
	 }
	    
	 return typeAdapter;
  }
  
  /*******************************************************************/
  
  public static class ObjectType extends OrmType<Object> {
    @Override
    public void toValues(ContentValues values, String column, Object value) {
      if (value == null)
        values.putNull(column);
      else
        values.put(column, value.toString());
    }
    @Override
    public Object fromResult(Cursor c, String column, int index) {
      return c.getString(index);
    }
  }
  
  public static class BoolType extends OrmType<Boolean> {
    @Override
    public void toValues(ContentValues values, String column, Boolean value) {
      if (value == null)
        values.putNull(column);
      else
        values.put(column, (value) ? 1 : 0);
    }
    @Override
    public Boolean fromResult(Cursor c, String column, int index) {
      Integer val = c.getInt(index);
      if (val != null) {
        return val == 1;
      }
      return false;
    }
  }
  
  public static class StringType extends OrmType<String> {
    @Override
    public void toValues(ContentValues values, String column, String value) {
      if (value == null)
        values.putNull(column);
      else
        values.put(column, value);
    }
    @Override
    public String fromResult(Cursor c, String column, int index) {
      return c.getString(index);
    }
  }
  
  public static class ShortType extends OrmType<Short> {
    @Override
    public void toValues(ContentValues values, String column, Short value) {
      if (value == null)
        values.putNull(column);
      else
        values.put(column, value);
    }
    @Override
    public Short fromResult(Cursor c, String column, int index) {
      return c.getShort(index);
    }
  }
  
  public static class IntType extends OrmType<Integer> {
    @Override
    public void toValues(ContentValues values, String column, Integer value) {
      if (value == null)
        values.putNull(column);
      else
        values.put(column, value);
    }
    @Override
    public Integer fromResult(Cursor c, String column, int index) {
      return c.getInt(index);
    }
  }
  
  public static class LongType extends OrmType<Long> {
    @Override
    public void toValues(ContentValues values, String column, Long value) {
      if (value == null)
        values.putNull(column);
      else
        values.put(column, value);
    }
    @Override
    public Long fromResult(Cursor c, String column, int index) {
      return c.getLong(index);
    }
  }
  
  public static class FloatType extends OrmType<Float> {
    @Override
    public void toValues(ContentValues values, String column, Float value) {
      if (value == null)
        values.putNull(column);
      else
        values.put(column, value);
    }
    @Override
    public Float fromResult(Cursor c, String column, int index) {
      return c.getFloat(index);
    }
  }
  
  public static class DoubleType extends OrmType<Double> {
    @Override
    public void toValues(ContentValues values, String column, Double value) {
      if (value == null)
        values.putNull(column);
      else
        values.put(column, value);
    }
    @Override
    public Double fromResult(Cursor c, String column, int index) {
      return c.getDouble(index);
    }
  }
  
  public static class BytesType extends OrmType<byte[]> {
    @Override
    public void toValues(ContentValues values, String column, byte[] value) {
      if (value == null)
        values.putNull(column);
      else
        values.put(column, value);
    }
    @Override
    public byte[] fromResult(Cursor c, String column, int index) {
      return c.getBlob(index);
    }
  }
  
  public static class DateType extends OrmType<Date> {
    @Override
    public void toValues(ContentValues values, String column, Date value) {
      if (value == null)
        values.putNull(column);
      else
        values.put(column, value.getTime());
    }
    @Override
    public Date fromResult(Cursor c, String column, int index) {
      Long time = c.getLong(index);
      if (time != null) {
        return new Date(time);
      }
      return null;
    }
  }
}
