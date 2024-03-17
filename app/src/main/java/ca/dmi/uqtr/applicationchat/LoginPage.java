package ca.dmi.uqtr.applicationchat;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.material.tabs.TabLayout;

import ca.dmi.uqtr.applicationchat.databinding.LoginPageBinding;
import ca.dmi.uqtr.applicationchat.services.ApiService;
import ca.dmi.uqtr.applicationchat.fragments.FragmentAdapter;

public class LoginPage extends AppCompatActivity {

    private LoginPageBinding binding; // Liaison de données pour la mise en page
    ViewPager2 viewPage; // ViewPager2 pour afficher les fragments
    private FragmentAdapter adapter; // Adaptateur pour gérer les fragments
    private TabLayout tabLayout; // TabLayout pour les onglets
    private ApiService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent serviceIntent = new Intent(this, ApiService.class);
        this.startService(serviceIntent);
        this.bindService(serviceIntent, apiServiceConnection, Context.BIND_AUTO_CREATE);


        binding = DataBindingUtil.setContentView(this, R.layout.login_page);
        viewPage = binding.viewPager;

        //Utilisation de l'adaptateur pour gérer les fragments
        viewPage.setAdapter(new FragmentAdapter(this));

        // Initialisation du TabLayout
        tabLayout = binding.tabLayout;

        // Ajout des onglets au TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Sign In"));
        tabLayout.addTab(tabLayout.newTab().setText("Sign Up"));
        tabLayout.addTab(tabLayout.newTab().setText("Reset Password"));

        // Écouteur pour gérer la sélection des onglets
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPage.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //  Écouteur pour gérer le changement de page dans ViewPager2
        viewPage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Lorsqu'une page est sélectionnée, sélectionnez l'onglet correspondant dans TabLayout
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    private ServiceConnection apiServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ApiService.LocalBinder binder = (ApiService.LocalBinder) iBinder;
            apiService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            apiService = null;
        }
    };
}
