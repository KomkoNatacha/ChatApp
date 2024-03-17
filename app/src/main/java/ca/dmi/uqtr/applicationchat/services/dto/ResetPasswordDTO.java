package ca.dmi.uqtr.applicationchat.services.dto;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.dmi.uqtr.applicationchat.BR;

public class ResetPasswordDTO extends BaseObservable {
    private String username;
    private String email;
    private String newpassword;
    private String confNewPassword;
    @Bindable
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
        notifyPropertyChanged(BR.newpassword);
    }
    @Bindable
    public String getConfNewPassword() {
        return confNewPassword;
    }

    public void setConfNewPassword(String confNewPassword) {
        this.confNewPassword = confNewPassword;
        notifyPropertyChanged(BR.confNewPassword);
    }
}
