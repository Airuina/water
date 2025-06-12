package com.example.waterreminder;

import android.database.Cursor;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class DrinkDao_Impl implements DrinkDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DrinkRecord> __insertionAdapterOfDrinkRecord;

  public DrinkDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDrinkRecord = new EntityInsertionAdapter<DrinkRecord>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `drink_records` (`id`,`timestamp`,`amount`,`type`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DrinkRecord value) {
        stmt.bindLong(1, value.id);
        stmt.bindLong(2, value.timestamp);
        stmt.bindLong(3, value.amount);
        if (value.type == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.type);
        }
      }
    };
  }

  @Override
  public void insert(final DrinkRecord record) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfDrinkRecord.insert(record);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int getTodayCount(final long startMillis) {
    final String _sql = "SELECT COUNT(*) FROM drink_records WHERE timestamp >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startMillis);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if(_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<DrinkRecord> getRecordsBetween(final long start, final long end) {
    final String _sql = "SELECT * FROM drink_records WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final List<DrinkRecord> _result = new ArrayList<DrinkRecord>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final DrinkRecord _item;
        _item = new DrinkRecord();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        _item.timestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        _item.amount = _cursor.getInt(_cursorIndexOfAmount);
        if (_cursor.isNull(_cursorIndexOfType)) {
          _item.type = null;
        } else {
          _item.type = _cursor.getString(_cursorIndexOfType);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int getCountBetween(final long start, final long end) {
    final String _sql = "SELECT COUNT(*) FROM drink_records WHERE timestamp BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, start);
    _argIndex = 2;
    _statement.bindLong(_argIndex, end);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if(_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<DrinkRecord> getAllRecords() {
    final String _sql = "SELECT * FROM drink_records ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final List<DrinkRecord> _result = new ArrayList<DrinkRecord>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final DrinkRecord _item;
        _item = new DrinkRecord();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        _item.timestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        _item.amount = _cursor.getInt(_cursorIndexOfAmount);
        if (_cursor.isNull(_cursorIndexOfType)) {
          _item.type = null;
        } else {
          _item.type = _cursor.getString(_cursorIndexOfType);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int getTotalAmountBetween(final long startTime, final long endTime) {
    final String _sql = "SELECT SUM(amount) FROM drink_records WHERE timestamp BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if(_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
