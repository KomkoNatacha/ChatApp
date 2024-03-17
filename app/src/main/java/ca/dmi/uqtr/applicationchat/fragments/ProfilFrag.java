package ca.dmi.uqtr.applicationchat.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import ca.dmi.uqtr.applicationchat.bdlocal.model.UserInfo;
import ca.dmi.uqtr.applicationchat.databinding.FragmentProfilBinding;
import ca.dmi.uqtr.applicationchat.preferences.UserPreferences;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfilFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilFrag extends Fragment {

    private FragmentProfilBinding binding;
    private UserPreferences userPreferences;
    private UserInfo userInfo;


    private FirebaseFirestore db;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfilFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfilFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfilFrag newInstance(String param1, String param2) {
        ProfilFrag fragment = new ProfilFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreferences = new UserPreferences(requireContext());
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfilBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        userPreferences = new UserPreferences(requireContext());
        userInfo = new UserInfo();
        binding.setViewModel(userInfo);
        getUserDetails();
        return view;
    }
    private void getUserDetails() {
        String userId = userPreferences.getUserId();

        if (userId != null) {
            db.collection("/users").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (userId.equals(document.getString("userId"))) {
                                    String username = document.getString("username");
                                    String email = document.getString("email");
                                    String firstName = document.getString("firstName");
                                    String nickname = document.getString("nickname");
                                    String lastName = document.getString("lastName");

                                    userInfo.setUsername("Username : " +username);
                                    userInfo.setEmail("Email : " +email);
                                    userInfo.setFirstName("FirstName : " +firstName);
                                    userInfo.setNickname("Nickname : " +nickname);
                                    userInfo.setLastName("LastName : " +lastName);
                                    binding.executePendingBindings();
                                    break;
                                }
                            }
                        }
                    });
        }

    }}