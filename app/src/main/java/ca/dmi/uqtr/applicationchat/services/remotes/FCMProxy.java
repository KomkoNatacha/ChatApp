package ca.dmi.uqtr.applicationchat.services.remotes;


import ca.dmi.uqtr.applicationchat.BuildConfig;

import ca.dmi.uqtr.applicationchat.services.dto.FcmMessageDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMProxy {

    @Headers({"Content-Type: application/json",
            "Authorization: Key=" + BuildConfig.FCM_API_KEY})


    @POST("fcm/send")
    Call<ResponseBody> sendFcm(@Body FcmMessageDTO message);


}
