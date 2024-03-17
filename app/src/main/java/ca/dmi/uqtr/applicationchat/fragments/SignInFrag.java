package ca.dmi.uqtr.applicationchat.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import ca.dmi.uqtr.applicationchat.MainPage;
import ca.dmi.uqtr.applicationchat.R;
import ca.dmi.uqtr.applicationchat.databinding.FragmentSignInBinding;
import ca.dmi.uqtr.applicationchat.preferences.UserPreferences;
import ca.dmi.uqtr.applicationchat.services.ApiService;
import ca.dmi.uqtr.applicationchat.services.dto.SignInDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignInFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentSignInBinding binding;
    private ApiService apiService;
    private UserPreferences userPreferences;
    SignInDTO signInDTO;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignInFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignInFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static SignInFrag newInstance(String param1, String param2) {
        SignInFrag fragment = new SignInFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent serviceIntent = new Intent(requireContext(), ApiService.class);
        requireContext().startService(serviceIntent);
        requireContext().bindService(serviceIntent, apiServiceConnection, Context.BIND_AUTO_CREATE);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in, container, false);
        View view = binding.getRoot();


        signInDTO = new SignInDTO();
        binding.setExistUserinfo(new SignInDTO());
        binding.setLifecycleOwner(this);
        userPreferences = new UserPreferences(requireContext());

        binding.progressBar.setVisibility(View.GONE);

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        binding.textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirige vers la page d'inscription
                ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
                if (viewPager != null) {
                    viewPager.setCurrentItem(1, true);
                }
            }
        });

        binding.forgetPasswordt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
                if (viewPager != null) {
                    viewPager.setCurrentItem(3, true);
                }
            }
        });
        return view;
    }


    private void signIn() {
        binding.progressBar.setVisibility(View.VISIBLE);

        signInDTO = new SignInDTO();
        signInDTO.setUsername(binding.editUsername.getText().toString());
        signInDTO.setPassword(binding.editMotDePass.getText().toString());


        // Vérifions que tous les champs sont remplis

        if (TextUtils.isEmpty(signInDTO.getUsername()) || TextUtils.isEmpty(signInDTO.getPassword())) {
            Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

        apiService.signIn(signInDTO, new Callback<SignInDTO>() {
            @Override
            public void onResponse(Call<SignInDTO> call, Response<SignInDTO> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    String authToken = response.headers().get("Authorization");
                    userPreferences.setAuthToken(authToken);

                    SignInDTO signInResponse = response.body();
                    userPreferences.setUserId(signInResponse.getUserId());
                    userPreferences.setUsername(signInResponse.getUsername());
                    userPreferences.setPassword(signInResponse.getPassword());

                    Toast.makeText(requireContext(), "Bienvenue", Toast.LENGTH_SHORT).show();
                    openMainPage();
                } else {
                    Toast.makeText(requireContext(), "Échec de l'authentification", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<SignInDTO> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }

        });
    }


    @Override
    public void onResume() {
        super.onResume();
        binding.editUsername.setText("");
        binding.editMotDePass.setText("");
        binding.progressBar.setVisibility(View.GONE);
    }

    private void openMainPage() {
        Intent intent = new Intent(requireContext(), MainPage.class);
        startActivity(intent);
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