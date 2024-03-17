package ca.dmi.uqtr.applicationchat.bdlocal;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Transaction;
import androidx.room.TypeConverters;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import ca.dmi.uqtr.applicationchat.bdlocal.converters.Converters;
import ca.dmi.uqtr.applicationchat.bdlocal.dao.MessageDao;
import ca.dmi.uqtr.applicationchat.bdlocal.dao.UserDao;
import ca.dmi.uqtr.applicationchat.bdlocal.model.Message;
import ca.dmi.uqtr.applicationchat.bdlocal.model.UserInfo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Database(entities = {UserInfo.class, Message.class}, version = 6)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {


    public static final Migration MIGRATION_3_4 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

        }
    };

    private final ExecutorService writingPool = Executors.newSingleThreadExecutor();
    private final ExecutorService readingPool = Executors.newFixedThreadPool(4);
    private Context context;
    public static final Executor databaseWriteExecutor = Executors.newFixedThreadPool(4);

    private static AppDatabase instance;

    public abstract MessageDao messageDao();
    public abstract UserDao userDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context, AppDatabase.class, "app_database")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // instance.insert();
                                }
                            })
                            .build();
                    instance.context = context;
                }
            }
        }
        return instance;
    }

    @Transaction
    public void insert(Message... messages) {
        messageDao().insert(messages);
    }
    @Transaction
    public void insertMessageForUserAndContact(Message message) {
        messageDao().insertMessageForUserAndContact( message);
    }
    private void loadSQLRaw(@RawRes int sqlRaw) throws IOException {
        SupportSQLiteDatabase helper = getOpenHelper().getWritableDatabase();
        try (InputStream is = context.getResources().openRawResource(sqlRaw)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            String sql = new String(buffer, StandardCharsets.UTF_8);
            helper.execSQL(sql);
        }
    }

    public void read(Runnable task) {
        readingPool.submit(task);
    }

    public void write(Runnable task) {
        writingPool.submit(task);
    }
}
