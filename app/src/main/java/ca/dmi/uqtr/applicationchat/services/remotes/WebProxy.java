package ca.dmi.uqtr.applicationchat.services.remotes;

import java.util.List;

import ca.dmi.uqtr.applicationchat.bdlocal.model.Message;
import ca.dmi.uqtr.applicationchat.services.dto.ResetPasswordDTO;
import ca.dmi.uqtr.applicationchat.services.dto.SignInDTO;
import ca.dmi.uqtr.applicationchat.services.dto.SignUpDTO;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface WebProxy {
    // Méthode pour l'inscription d'un utilisateur
    @POST("/signup")
    Call<SignUpDTO> signUp(@Body SignUpDTO signUpDTO);

    // Méthode pour la connexion d'un utilisateur
    @POST("/signin")
    Call<SignInDTO> signIn(@Body SignInDTO signInDTO);

    // Méthode pour la réinitialisation du mot de passe
    @POST("/resetpassword")
    Call<ResetPasswordDTO> resetPassword(@Body ResetPasswordDTO resetPasswordDTO);



    @Multipart
    @POST("/uploadImage")
    Call<ResponseBody> uploadImage(
            @Part MultipartBody.Part image
    );

    @Multipart
    @POST("/uploadAudio")
    Call<ResponseBody> uploadAudio(@Part("audio") String audioFilePath, @Part MultipartBody.Part audio);

    @POST("/sendMessage")
    Call<ResponseBody> sendMessage(@Body Message message);

    @GET("/getMessages/{contactId}/{userId}")
    Call<List<Message>> getMessages(
            @Path("contactId") String contactId,
            @Path("userId") String userId

    );
}





