package com.example.waterreminder;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DrinkDao {
    @Query("SELECT COUNT(*) FROM drink_records WHERE timestamp >= :startMillis")
    int getTodayCount(long startMillis);

    @Insert
    void insert(DrinkRecord record);

    @Query("SELECT * FROM drink_records WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp ASC")
    List<DrinkRecord> getRecordsBetween(long start, long end);

    @Query("SELECT COUNT(*) FROM drink_records WHERE timestamp BETWEEN :start AND :end")
    int getCountBetween(long start, long end);

    @Query("SELECT * FROM drink_records ORDER BY timestamp ASC")
    List<DrinkRecord> getAllRecords();

    @Query("SELECT SUM(amount) FROM drink_records WHERE timestamp BETWEEN :startTime AND :endTime")
    int getTotalAmountBetween(long startTime, long endTime);
}

