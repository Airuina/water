package com.example.waterreminder;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {DrinkRecord.class}, version = 3, exportSchema = false)
public abstract class DrinkDatabase extends RoomDatabase {

    private static volatile DrinkDatabase instance;

    public abstract DrinkDao drinkDao();

    // 数据库迁移策略
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 创建临时表
            database.execSQL("CREATE TABLE drink_records_temp ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "timestamp INTEGER NOT NULL DEFAULT 0, "
                + "amount INTEGER NOT NULL DEFAULT 350, "
                + "type TEXT NOT NULL DEFAULT '白水')");
            
            // 复制数据
            database.execSQL("INSERT INTO drink_records_temp (id, timestamp) "
                + "SELECT id, timestamp FROM drink_records");
            
            // 删除旧表
            database.execSQL("DROP TABLE drink_records");
            
            // 重命名新表
            database.execSQL("ALTER TABLE drink_records_temp RENAME TO drink_records");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 更新列的默认值和非空约束
            database.execSQL("CREATE TABLE drink_records_temp ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + "timestamp INTEGER NOT NULL DEFAULT 0, "
                + "amount INTEGER NOT NULL DEFAULT 350, "
                + "type TEXT NOT NULL DEFAULT '白水')");
            
            // 复制数据
            database.execSQL("INSERT INTO drink_records_temp (id, timestamp, amount, type) "
                + "SELECT id, timestamp, amount, type FROM drink_records");
            
            // 删除旧表
            database.execSQL("DROP TABLE drink_records");
            
            // 重命名新表
            database.execSQL("ALTER TABLE drink_records_temp RENAME TO drink_records");
        }
    };

    public static DrinkDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (DrinkDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            DrinkDatabase.class,
                            "drink_db"
                    )
                    .fallbackToDestructiveMigration()  // 如果迁移失败，允许重建数据库
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build();
                }
            }
        }
        return instance;
    }
}
