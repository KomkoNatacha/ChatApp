package ca.dmi.uqtr.applicationchat.viewmodels;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ca.dmi.uqtr.applicationchat.bdlocal.model.UserInfo;

public class ContactViewModel extends ViewModel {
    private final MutableLiveData<String> selectedUserId = new MutableLiveData<>();
    private final MutableLiveData<String> selectedUserName = new MutableLiveData<>();
    private final MutableLiveData<String> selectedUserProfilePhoto = new MutableLiveData<>();
    private final MutableLiveData<UserInfo> selectedUserLiveData;

    public ContactViewModel() {
        selectedUserLiveData = new MutableLiveData<>();
    }

    public void setSelectedUserInfo(String userId, String firstName, String imageUrl) {
        UserInfo user = new UserInfo(userId, firstName, imageUrl);
        selectedUserLiveData.setValue(user);
        selectedUserName.setValue(firstName);
        selectedUserProfilePhoto.setValue(imageUrl);
        selectedUserId.setValue(userId);
    }


    public LiveData<String> getSelectedUserIdLiveData() {
        return selectedUserId;
    }

    public LiveData<String> getSelectedUserNameLiveData() {
        return selectedUserName;
    }

    public LiveData<String> getSelectedUserProfilePhotoLiveData() {
        return selectedUserProfilePhoto;
    }

    public LiveData<UserInfo> getSelectedUserLiveData() {
        return selectedUserLiveData;
    }



}
