package com.example.waterreminder;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import androidx.room.ColumnInfo;

@Entity(tableName = "drink_records")
public class DrinkRecord {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "timestamp", defaultValue = "0")
    public long timestamp;
    
    @ColumnInfo(name = "amount", defaultValue = "350")
    public int amount;  // 饮水量（ml）
    
    @ColumnInfo(name = "type", defaultValue = "'白水'")
    public String type;  // 饮品类型（白水、咖啡、茶）

    public DrinkRecord() {
        // Room needs a public no-arg constructor
    }

    @Ignore
    public DrinkRecord(long timestamp, int amount, String type) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.type = type;
    }
}

