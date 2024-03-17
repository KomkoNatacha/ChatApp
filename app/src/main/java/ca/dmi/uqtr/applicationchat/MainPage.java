package ca.dmi.uqtr.applicationchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import ca.dmi.uqtr.applicationchat.bdlocal.model.UserInfo;
import ca.dmi.uqtr.applicationchat.databinding.MainPageBinding;
import ca.dmi.uqtr.applicationchat.fragments.ChatScreen;
import ca.dmi.uqtr.applicationchat.fragments.ContactFrag;
import ca.dmi.uqtr.applicationchat.fragments.ProfilFrag;
import ca.dmi.uqtr.applicationchat.fragments.SettingFrag;
import ca.dmi.uqtr.applicationchat.viewmodels.ContactViewModel;

public class MainPage extends AppCompatActivity {
    private ContactViewModel contactViewModel;

    private MainPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.main_page);
        binding.setLifecycleOwner(this);
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

        contactViewModel.getSelectedUserLiveData().observe(this, selectedUser -> {
            if (selectedUser != null) {
                openChatScreen(selectedUser.getUserId());
            }
        });


        addFragment(new ContactFrag());


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                FrameLayout frameLayout = findViewById(R.id.frame);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment f = null;

                switch (item.getItemId()) {
                    case R.id.navigation_messages:
                        f = new ContactFrag();
                        break;
                    case R.id.navigation_profile:
                        f = new ProfilFrag();
                        break;
                    case R.id.navigation_settings:
                        f = new SettingFrag();
                        break;
                }

                if (f != null) {
                    frameLayout.removeAllViews();
                    fragmentTransaction.add(R.id.frame, f);
                    fragmentTransaction.commit();

                }

                return false;
            }
        });
    }

    private void addFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame, fragment)
                .commit();
    }


    private void openChatScreen(String userId) {
        findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);

        UserInfo selectedUser = contactViewModel.getSelectedUserLiveData().getValue();
        String userName = selectedUser.getFirstName();
        String userProfilePhoto = selectedUser.getImageUrl();
        ChatScreen chatScreen = ChatScreen.newInstance(userId, userName, userProfilePhoto);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, chatScreen)
                .addToBackStack(null)
                .commit();
    }


    public MainPageBinding getBinding() {
        return binding;
    }
}
