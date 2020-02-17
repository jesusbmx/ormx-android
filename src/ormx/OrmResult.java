package ormx;

import java.io.Closeable;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

public class OrmResult implements Cursor, Closeable {

	final Cursor cursor;

	public OrmResult(Cursor cursor) {
		this.cursor = cursor;
	}

	public <V> List<V> list(OrmObjectAdapter<V> adapter) {
		try {
			return adapter.list(cursor);
		} finally {
			close();
		}
	}

	public <V> List<V> list(Class<V> classOf) {
		OrmObjectAdapter<V> adapter = OrmObjectAdapter.of(classOf);
		return list(adapter);
	}

	public <V> V row(OrmObjectAdapter<V> adapter) {
		try {
			return adapter.entity(cursor);
		} finally {
			close();
		}
	}

	public <V> V row(Class<V> classOf) {
		OrmObjectAdapter<V> adapter = OrmObjectAdapter.of(classOf);
		return row(adapter);
	}

	public <V> OrmIterator<V> iterator(OrmObjectAdapter<V> adapter) {
		return adapter.iterator(cursor);
	}

	public <V> OrmIterator<V> iterator(Class<V> classOf) {
		OrmObjectAdapter<V> adapter = OrmObjectAdapter.of(classOf);
		return iterator(adapter);
	}

	public int getCount() {
		return cursor.getCount();
	}

	public int getPosition() {
		return cursor.getPosition();
	}

	public boolean move(int offset) {
		return cursor.move(offset);
	}

	public boolean moveToPosition(int position) {
		return cursor.moveToPosition(position);
	}

	public boolean moveToFirst() {
		return cursor.moveToFirst();
	}

	public boolean moveToLast() {
		return cursor.moveToLast();
	}

	public boolean moveToNext() {
		return cursor.moveToNext();
	}

	public boolean moveToPrevious() {
		return cursor.moveToPrevious();
	}

	public boolean isFirst() {
		return cursor.isFirst();
	}

	public boolean isLast() {
		return cursor.isLast();
	}

	public boolean isBeforeFirst() {
		return cursor.isBeforeFirst();
	}

	public boolean isAfterLast() {
		return cursor.isAfterLast();
	}

	public int getColumnIndex(String columnName) {
		return cursor.getColumnIndex(columnName);
	}

	public int getColumnIndexOrThrow(String columnName)
			throws IllegalArgumentException {
		return cursor.getColumnIndexOrThrow(columnName);
	}

	public String getColumnName(int columnIndex) {
		return cursor.getColumnName(columnIndex);
	}

	public String[] getColumnNames() {
		return cursor.getColumnNames();
	}

	public int getColumnCount() {
		return cursor.getColumnCount();
	}

	public byte[] getBlob(int columnIndex) {
		return cursor.getBlob(columnIndex);
	}

	public String getString(int columnIndex) {
		return cursor.getString(columnIndex);
	}

	public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
		cursor.copyStringToBuffer(columnIndex, buffer);
	}

	public short getShort(int columnIndex) {
		return cursor.getShort(columnIndex);
	}

	public int getInt(int columnIndex) {
		return cursor.getInt(columnIndex);
	}

	public long getLong(int columnIndex) {
		return cursor.getLong(columnIndex);
	}

	public float getFloat(int columnIndex) {
		return cursor.getFloat(columnIndex);
	}

	public double getDouble(int columnIndex) {
		return cursor.getDouble(columnIndex);
	}

	@SuppressLint("NewApi")
	public int getType(int columnIndex) {
		return cursor.getType(columnIndex);
	}

	public boolean isNull(int columnIndex) {
		return cursor.isNull(columnIndex);
	}

	@Deprecated
	public void deactivate() {
		cursor.deactivate();
	}

	@Deprecated
	public boolean requery() {
		return cursor.requery();
	}

	public boolean isClosed() {
		return cursor.isClosed();
	}

	public void registerContentObserver(ContentObserver observer) {
		cursor.registerContentObserver(observer);
	}

	public void unregisterContentObserver(ContentObserver observer) {
		cursor.unregisterContentObserver(observer);
	}

	public void registerDataSetObserver(DataSetObserver observer) {
		cursor.registerDataSetObserver(observer);
	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
		cursor.unregisterDataSetObserver(observer);
	}

	public void setNotificationUri(ContentResolver cr, Uri uri) {
		cursor.setNotificationUri(cr, uri);
	}

	@SuppressLint("NewApi")
	public Uri getNotificationUri() {
		return cursor.getNotificationUri();
	}

	public boolean getWantsAllOnMoveCalls() {
		return cursor.getWantsAllOnMoveCalls();
	}

	public Bundle getExtras() {
		return cursor.getExtras();
	}

	public Bundle respond(Bundle extras) {
		return cursor.respond(extras);
	}

	@Override
	public void close() {
		OrmUtils.close(cursor);
	}

}
