package com.example.news.data; // Thay thế bằng package của bạn

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {KeywordEntity.class, ArticleInteractionEntity.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AppDao appDao();

    // Biến instance volatile để đảm bảo an toàn luồng (thread safety)
    private static volatile AppDatabase INSTANCE;

    /**
     * Phương thức static để lấy instance của Database.
     * Sử dụng mẫu Singleton để đảm bảo chỉ có một instance duy nhất.
     */
    public static AppDatabase getDatabase(final Context context) {
        // Nếu instance chưa được tạo, thì mới vào khối synchronized
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                // Kiểm tra lại lần nữa bên trong khối synchronized
                // để tránh race condition
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}