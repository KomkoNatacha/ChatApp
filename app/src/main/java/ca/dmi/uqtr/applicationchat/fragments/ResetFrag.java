package ca.dmi.uqtr.applicationchat.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import ca.dmi.uqtr.applicationchat.R;

import ca.dmi.uqtr.applicationchat.databinding.FragmentResetBinding;
import ca.dmi.uqtr.applicationchat.services.ApiService;
import ca.dmi.uqtr.applicationchat.services.dto.ResetPasswordDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetFrag extends Fragment {
    private FragmentResetBinding binding;
    private ApiService apiService;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ResetFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResetFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static ResetFrag newInstance(String param1, String param2) {
        ResetFrag fragment = new ResetFrag();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset, container, false);
        View view = binding.getRoot();
        binding.setResetUserinfo(new ResetPasswordDTO());
        binding.setLifecycleOwner(this);
        binding.progressBarReset.setVisibility(View.GONE);
        binding.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });



        return view;
    }

    private void resetPassword() {

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();

        resetPasswordDTO.setUsername(binding.fgFirstname.getText().toString());
        resetPasswordDTO.setEmail(binding.fgEmail.getText().toString());
        resetPasswordDTO.setNewpassword(binding.edNewpassword.getText().toString());
        resetPasswordDTO.setConfNewPassword(binding.edConfNewpassword.getText().toString());


            if (TextUtils.isEmpty(resetPasswordDTO.getUsername()) ||
                    TextUtils.isEmpty(resetPasswordDTO.getEmail()) ||
                    TextUtils.isEmpty(resetPasswordDTO.getNewpassword()) ||
                    TextUtils.isEmpty(resetPasswordDTO.getConfNewPassword())) {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                binding.progressBarReset.setVisibility(View.GONE);
                return;
            }



        apiService.resetPassword(resetPasswordDTO, new Callback<ResetPasswordDTO>() {
            @Override
            public void onResponse(Call<ResetPasswordDTO> call, Response<ResetPasswordDTO> response) {
                binding.progressBarReset.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Mot de passe réinitialisé avec succès", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Échec de la réinitialisation du mot de passe.Verifier vos entrees.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordDTO> call, Throwable t) {
                binding.progressBarReset.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        binding.fgFirstname.setText("");
        binding.fgEmail.setText("");
        binding.edNewpassword.setText("");
        binding.edConfNewpassword.setText("");

        binding.progressBarReset.setVisibility(View.GONE);
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