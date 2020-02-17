package ormx;

import android.database.Cursor;

import java.io.Closeable;
import java.util.Iterator;

import ormx.OrmObjectAdapter;
import ormx.OrmUtils;


public class OrmIterator<T> implements Iterable<T>, Iterator<T>, Closeable {

  final OrmObjectAdapter<T> adapter;
  final Cursor cursor;
  
  private boolean next;

  public OrmIterator(OrmObjectAdapter<T> adapter, Cursor cursor) {
    this.adapter = adapter;
    this.cursor = cursor;
  }

  @Override public Iterator<T> iterator() {
    return this;
  }

  @Override public boolean hasNext() {
    try {
      return next = cursor.moveToNext();
    } catch (Exception e) {
      close();
      throw new RuntimeException(e.getMessage(), e);
    } finally {
      if (!next) close();
    }
  }

  @Override public T next() {
    try {
      return adapter.cursorToEntityOrThrow(cursor);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override public void remove() {
    
  }

  @Override public void close() {
    OrmUtils.close(cursor);
  }
}
