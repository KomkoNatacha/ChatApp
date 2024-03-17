package ca.dmi.uqtr.applicationchat.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.IBinder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;


import ca.dmi.uqtr.applicationchat.R;

import ca.dmi.uqtr.applicationchat.databinding.FragmentSignUpBinding;
import ca.dmi.uqtr.applicationchat.services.ApiService;
import ca.dmi.uqtr.applicationchat.services.dto.SignUpDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFrag extends Fragment {
    private FragmentSignUpBinding binding;
    private ApiService apiService;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignUpFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFrag newInstance(String param1, String param2) {
        SignUpFrag fragment = new SignUpFrag();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false);
        View view = binding.getRoot();

        binding.setNewUserinfo(new SignUpDTO());
        binding.setLifecycleOwner(this);


        binding.buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        binding.textAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
                if (viewPager != null) {
                    viewPager.setCurrentItem(0, true);
                }
            }
        });

        return view;
    }


    private void createAccount() {
        binding.progressBar.setVisibility(View.VISIBLE);


        SignUpDTO signUpDTO = new SignUpDTO();
        signUpDTO.setUsername(binding.edUsername.getText().toString());
        signUpDTO.setFirstName(binding.edFirstname.getText().toString());
        signUpDTO.setLastName(binding.edLastname.getText().toString());
        signUpDTO.setNickname(binding.edNickname.getText().toString());
        signUpDTO.setEmail(binding.edEmail.getText().toString());
        signUpDTO.setPassword(binding.edPassword.getText().toString());
        signUpDTO.setConfPassword(binding.edConfPassword.getText().toString());


        // Vérifions que tous les champs sont remplis
        if (TextUtils.isEmpty(signUpDTO.getUsername()) || TextUtils.isEmpty(signUpDTO.getFirstName())
                || TextUtils.isEmpty(signUpDTO.getLastName()) || TextUtils.isEmpty(signUpDTO.getNickname())
                || TextUtils.isEmpty(signUpDTO.getEmail()) || TextUtils.isEmpty(signUpDTO.getPassword())
                || TextUtils.isEmpty(signUpDTO.getConfPassword())) {
            Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

        if (!signUpDTO.getPassword().equals(signUpDTO.getConfPassword())) {
            Toast.makeText(requireContext(), "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }


        apiService.signUp(signUpDTO, new Callback<SignUpDTO>() {
            @Override
            public void onResponse(Call<SignUpDTO> call, Response<SignUpDTO> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    SignUpDTO signUpResponse = response.body();


                    // Compte créé avec succès
                    Toast.makeText(requireContext(), "Compte créé avec succès", Toast.LENGTH_SHORT).show();
                    ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
                    if (viewPager != null) {
                        viewPager.setCurrentItem(0, true);
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(requireContext(), "Échec de la création du compte", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SignUpDTO> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        SignUpDTO signUpDTO = binding.getNewUserinfo();
        signUpDTO.setUsername("");
        signUpDTO.setFirstName("");
        signUpDTO.setLastName("");
        signUpDTO.setNickname("");
        signUpDTO.setEmail("");
        signUpDTO.setPassword("");
        signUpDTO.setConfPassword("");

        binding.progressBar.setVisibility(View.GONE);
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