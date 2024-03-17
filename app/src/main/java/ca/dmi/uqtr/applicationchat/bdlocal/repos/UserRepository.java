package ca.dmi.uqtr.applicationchat.bdlocal.repos;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ca.dmi.uqtr.applicationchat.bdlocal.AppDatabase;
import ca.dmi.uqtr.applicationchat.bdlocal.dao.UserDao;
import ca.dmi.uqtr.applicationchat.bdlocal.model.UserInfo;

public class UserRepository {
    private final UserDao userDao;
    private final ExecutorService executorService;
    private final Handler handler;
    private static AppDatabase userDB;

    public UserRepository(Context context) {
        userDB = AppDatabase.getInstance(context);
        userDao = userDB.userDao();
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }

    public LiveData<List<UserInfo>> getAllUsers() {
        MutableLiveData<List<UserInfo>> result = new MutableLiveData<>();

        executorService.execute(() -> {
            List<UserInfo> users = userDao.getAllUsers();
            notifyOnMainThread(() -> result.setValue(users));
        });

        return result;
    }

    public void insertUsers(List<UserInfo> users) {
        executorService.execute(() -> {
            userDao.insertUsers(users);
        });
    }

    public void updateUser(UserInfo user) {
        executorService.execute(() -> {
            userDao.updateUser(user);
        });
    }

    public void deleteUser(UserInfo user) {
        executorService.execute(() -> {
            userDao.deleteUser(user);
        });
    }

    public void deleteAllUsers() {
        executorService.execute(() -> {
            userDao.deleteAllUsers();
        });
    }

    private void notifyOnMainThread(Runnable runnable) {
        handler.post(runnable);
    }
}
