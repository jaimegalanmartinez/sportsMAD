package com.mdp.sportsmad.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mdp.sportsmad.model.UserProfile;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<UserProfile> mUserProfile;
    public ProfileViewModel() {
        mUserProfile = new MutableLiveData<>();
    }

    public LiveData<UserProfile> getUserProfile() {
        return mUserProfile;
    }

    public void setmUserProfile(UserProfile userProfile) {
        mUserProfile.setValue(userProfile);
    }
}