package ca.dmi.uqtr.applicationchat.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Date;

import ca.dmi.uqtr.applicationchat.BuildConfig;
import ca.dmi.uqtr.applicationchat.MainPage;
import ca.dmi.uqtr.applicationchat.R;
import ca.dmi.uqtr.applicationchat.bdlocal.App;
import ca.dmi.uqtr.applicationchat.bdlocal.model.Message;
import ca.dmi.uqtr.applicationchat.services.dto.FcmMessageDTO;
import ca.dmi.uqtr.applicationchat.services.dto.NotificationDTO;
import ca.dmi.uqtr.applicationchat.services.remotes.FCMProxy;
import okhttp3.ResponseBody;
import retrofit2.Call;

import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FcmService extends FirebaseMessagingService {
    private App app;
    private static FcmService instance;

    @Override
    public void onCreate() {
        super.onCreate();
        app = (App) getApplication();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().containsKey(FcmMessageDTO.MESSAGE_KEY)) {
            String msgJson = remoteMessage.getData().get(FcmMessageDTO.MESSAGE_KEY);

            if (msgJson != null && !msgJson.isEmpty()) {
                Message message = (new Gson()).fromJson(msgJson, Message.class);

                if (!app.isInTop()) {
                    NotificationDTO notification = new NotificationDTO(
                            "Nouveau message",
                            message.getTest(),
                            message.getSenderId(),
                            "icon",
                            System.currentTimeMillis()
                    );

                    notifyUser(notification);
                }
            }
        }
    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.i("My token  : " , token);




    }

    // Méthode pour envoyer un message
    public void sendTextMessageViaFCM(FcmMessageDTO fcmMessageDTO, Callback<ResponseBody> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.FCM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FCMProxy fcmProxy = retrofit.create(FCMProxy.class);

        Call<ResponseBody> call = fcmProxy.sendFcm(fcmMessageDTO);
        call.enqueue(callback);
    }


    public static FcmService getInstance() {
        if (instance == null) {
            instance = new FcmService();
        }
        return instance;
    }


    private void notifyUser(NotificationDTO message) {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, App.CHANNEL_NOTIF)
                        .setSmallIcon(R.drawable.icon)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.icon))
                        .setContentTitle(message.title)
                        .setContentText(message.body)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .setSummaryText(message.body))

                        .setAutoCancel(true)
                        .setWhen(message.time)
                        .setContentIntent(pendingIntent());


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(message.issuer,0, notificationBuilder.build());
    }

    private PendingIntent pendingIntent() {
        Intent intent = new Intent(this, MainPage.class);
        return PendingIntent
                .getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}