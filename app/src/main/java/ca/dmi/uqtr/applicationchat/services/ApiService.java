package ca.dmi.uqtr.applicationchat.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ca.dmi.uqtr.applicationchat.BuildConfig;
import ca.dmi.uqtr.applicationchat.bdlocal.model.Message;

import ca.dmi.uqtr.applicationchat.services.dto.ResetPasswordDTO;
import ca.dmi.uqtr.applicationchat.services.dto.SignInDTO;
import ca.dmi.uqtr.applicationchat.services.dto.SignUpDTO;
import ca.dmi.uqtr.applicationchat.services.remotes.WebProxy;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService extends Service {

    private WebProxy proxy;
    private String imageUrl;
    private String audioUrl;


    public class LocalBinder extends Binder {
        public ApiService getService() {
            return ApiService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    public ApiService() {


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .callTimeout(90, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVICE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        proxy = retrofit.create(WebProxy.class);

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    // Méthode pour gérer l'authentification de l'utilisateur (signIn)
    public void signIn(SignInDTO dto, Callback<SignInDTO> callback) {
        proxy.signIn(dto).enqueue(callback);
    }


    // Méthode pour gérer la création d'un compte utilisateur (signUp)
    public void signUp(SignUpDTO dto, Callback<SignUpDTO> callback) {
        proxy.signUp(dto).enqueue(callback);
    }

    // Méthode pour gérer la réinitialisation de mot de passe (resetPassword)
    public void resetPassword(ResetPasswordDTO dto, Callback<ResetPasswordDTO> callback) {
        proxy.resetPassword(dto).enqueue(callback);
    }


    // Méthode pour envoyer l'image au serveur
    public void uploadImage(MultipartBody.Part image, Callback<ResponseBody> callback) {

        Call<ResponseBody> call = proxy.uploadImage(image);

        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        if (jsonObject.has("imageUrl")) {
                            imageUrl = jsonObject.getString("imageUrl");
                        } else {
                            imageUrl = "default_image_url";
                        }
                        callback.onResponse(call, response);
                    } catch (IOException | JSONException e) {
                        callback.onFailure(call, e);
                    }
                } else {
                    callback.onFailure(call, new Throwable("Image upload failed. Response code: " + response.code()));
                }
            }


            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    // Méthode pour envoyer l'audio au serveur
    public void uploadAudio(String audioFilePath, MultipartBody.Part audio, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = proxy.uploadAudio(audioFilePath, audio);

        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        if (jsonObject.has("audioUrl")) {
                            audioUrl = jsonObject.getString("audioUrl");
                        } else {
                        }
                        callback.onResponse(call, response);
                    } catch (IOException | JSONException e) {
                        callback.onFailure(call, e);
                    }
                } else {
                    callback.onFailure(call, new Throwable("Audio upload failed. Response code: " + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }


    public void sendMessage(Message glitchMessage, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = proxy.sendMessage(glitchMessage);
        call.enqueue(callback);
    }

    public void getMessages(String contactId, String userId, Callback<List<Message>> callback) {
        proxy.getMessages(contactId, userId).enqueue(callback);
    }

}
