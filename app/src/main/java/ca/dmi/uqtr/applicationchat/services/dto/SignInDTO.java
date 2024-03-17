package ca.dmi.uqtr.applicationchat.services.dto;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.dmi.uqtr.applicationchat.BR;

public class SignInDTO extends BaseObservable {
    private String username;
    private String password;
    private String userId;

    @Bindable
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }
}
