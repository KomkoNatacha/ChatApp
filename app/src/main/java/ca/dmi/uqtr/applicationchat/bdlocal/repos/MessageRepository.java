package ca.dmi.uqtr.applicationchat.bdlocal.repos;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import java.util.List;

import java.util.function.Consumer;

import ca.dmi.uqtr.applicationchat.bdlocal.dao.MessageDao;
import ca.dmi.uqtr.applicationchat.bdlocal.AppDatabase;
import ca.dmi.uqtr.applicationchat.bdlocal.model.Message;

public class MessageRepository {
    private final MessageDao messageDao;
    private final AppDatabase appDatabase;



    public MessageRepository(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
        this.messageDao = appDatabase.messageDao();
    }



    public void getAsyncAll(Consumer<List<Message>> completion) {
        appDatabase.read(() -> {
            List<Message> messages = messageDao.getAll();
            if (completion != null) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    completion.accept(messages);
                });
            }
        });
    }

    public LiveData<List<Message>> liveAll() {
        return messageDao.getAllMessages();
    }


    public void insert(Message... messages) {
        insert(null, messages);
    }

    public void insert(Runnable completion, Message... messages) {
        appDatabase.write(() -> {
            messageDao.insert(messages);
            if (completion != null) {
                new Handler(Looper.getMainLooper()).post(completion);
            }
        });
    }



    public void delete(Message... messages) {
        delete(null, messages);
    }

    public void delete(Runnable completion, Message... messages) {
        appDatabase.write(() -> {
            messageDao.delete(messages);

            if (completion != null) {
                new Handler(Looper.getMainLooper()).post(completion);
            }
        });
    }
}