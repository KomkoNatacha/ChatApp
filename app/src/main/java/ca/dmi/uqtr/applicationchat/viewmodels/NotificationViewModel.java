package ca.dmi.uqtr.applicationchat.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.dmi.uqtr.applicationchat.BR;

public class NotificationViewModel extends BaseObservable {
    private int iconRes;
    private String title;
    private String message;

    public NotificationViewModel(int iconRes, String title, String message) {
        this.iconRes = iconRes;
        this.title = title;
        this.message = message;
    }


    @Bindable
    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
        notifyPropertyChanged(BR.iconRes);
    }


    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        notifyPropertyChanged(BR.message);
    }
}
