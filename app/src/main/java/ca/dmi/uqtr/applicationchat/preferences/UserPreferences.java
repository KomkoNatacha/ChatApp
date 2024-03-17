package ca.dmi.uqtr.applicationchat.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {

    private static final String PREFERENCES_NAME = "UserPreferences";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String USER_ID = "userId";
    private static final String KEY_AUTH_TOKEN = "authToken";


    private final SharedPreferences preferences;

    public UserPreferences(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public String getUsername() {
        return preferences.getString(USERNAME, "");
    }

    public void setUsername(String username) {
        preferences.edit().putString(USERNAME, username).apply();
    }

    public String getPassword() {
        return preferences.getString(PASSWORD, "");
    }

    public void setPassword(String password) {
        preferences.edit().putString(PASSWORD, password).apply();
    }

    public String getUserId() {
        return preferences.getString(USER_ID, "");
    }

    public void setUserId(String userId) {
        preferences.edit().putString(USER_ID, userId).apply();
    }


    public String getEmail() {
        return preferences.getString("email", "");
    }

    public String getFirstName() {
        return preferences.getString("firstName", "");
    }

    public String getLastName() {
        return preferences.getString("lastName", "");
    }

    public String getNickname() {
        return preferences.getString("email", "");
    }

    public void setAuthToken(String authToken) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.apply();
    }

    public String getAuthToken() {
        return preferences.getString(KEY_AUTH_TOKEN, null);
    }

}

