package ca.dmi.uqtr.applicationchat.bdlocal;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import ca.dmi.uqtr.applicationchat.bdlocal.dao.MessageDao;
import ca.dmi.uqtr.applicationchat.bdlocal.dao.UserDao;

public class App extends MultiDexApplication  implements Application.ActivityLifecycleCallbacks{

    private static AppDatabase sAppDatabase;
    private MessageDao messageDao;
    private UserDao userDao;
    public static final String CHANNEL_NOTIF = "Notification_app";
    public static final  String topic = "Salon";
    private ViewModelStoreOwner storeOwner;
    private ViewModelProvider.AndroidViewModelFactory vmFactory;
    private Activity currentActivity;





    @Override
    public void onCreate() {
        super.onCreate();
        initDatabase();
        initChannel();
        subscribe();
        storeOwner = ViewModelStore::new;
        vmFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(this);
        registerActivityLifecycleCallbacks(this);
        

        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }

    private void initDatabase() {
        sAppDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app_database")
                .fallbackToDestructiveMigration()
                .build();

        messageDao = sAppDatabase.messageDao();
        userDao = sAppDatabase.userDao();
    }

    public static AppDatabase getAppDatabase() {
        if (sAppDatabase == null) {
            throw new IllegalStateException("AppDatabase not initialized. Call initDatabase() first.");
        }
        return sAppDatabase;
    }
    private void initChannel() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationChannel channel = new NotificationChannel(CHANNEL_NOTIF,
                "Canal de notification",
                NotificationManager.IMPORTANCE_HIGH);
        channel.enableVibration(true);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.setVibrationPattern(new long[]{200, 100});
        channel.setSound(RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                Notification.AUDIO_ATTRIBUTES_DEFAULT);

        notificationManager.createNotificationChannel(channel);

    }
    public MessageDao getMessageDao() {
        if (messageDao == null) {
            messageDao = getAppDatabase().messageDao();
        }
        return messageDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }


    public void subscribe() {
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    public ViewModelStoreOwner getStoreOwner(){
        return storeOwner;
    }
    public ViewModelProvider.AndroidViewModelFactory getFactory(){
        return  vmFactory;
    }

    public boolean isInTop() {
        return currentActivity != null;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        this.currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        if(currentActivity == activity){
            this.currentActivity = null;
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
