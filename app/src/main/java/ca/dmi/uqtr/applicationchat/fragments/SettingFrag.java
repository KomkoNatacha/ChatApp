package ca.dmi.uqtr.applicationchat.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import ca.dmi.uqtr.applicationchat.R;



public class SettingFrag extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

}