package ca.dmi.uqtr.applicationchat.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import ca.dmi.uqtr.applicationchat.LoginPage;
import ca.dmi.uqtr.applicationchat.R;
import ca.dmi.uqtr.applicationchat.bdlocal.adapter.UserAdapter;
import ca.dmi.uqtr.applicationchat.bdlocal.model.UserInfo;
import ca.dmi.uqtr.applicationchat.databinding.FragmentContactBinding;
import ca.dmi.uqtr.applicationchat.preferences.UserPreferences;
import ca.dmi.uqtr.applicationchat.viewmodels.ContactViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentContactBinding binding;
    private List<UserInfo> userList;
    private RecyclerView myRecyclerView;
    private UserAdapter adapter;
    private UserPreferences userPreferences;
    private CollectionReference usersRef;
    private ImageButton logoutButton;
    private ContactViewModel contactViewModel;


    public ContactFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFrag newInstance(String param1, String param2) {
        ContactFrag fragment = new ContactFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact, container, false);
        View view = binding.getRoot();

        contactViewModel = new ViewModelProvider(requireActivity()).get(ContactViewModel.class);
        myRecyclerView = binding.myLists;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 1);
        myRecyclerView.setLayoutManager(layoutManager);
        userPreferences = new UserPreferences(requireContext());

        userList = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usersRef = db.collection("/users");

        String currentUserId = userPreferences.getUserId();

        usersRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }

            if (queryDocumentSnapshots != null) {
                userList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    UserInfo user = document.toObject(UserInfo.class);

                    if (!user.getUserId().equals(currentUserId)) {
                        userList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new UserAdapter(userList, user -> {
            handleUserSelection(user);
        });


        myRecyclerView.setAdapter(adapter);


        logoutButton = binding.logoutButton;
        logoutButton.setOnClickListener(v -> logout());
        return view;
    }

    private void handleUserSelection(UserInfo user) {

        contactViewModel.setSelectedUserInfo(user.getUserId(), user.getFirstName(), user.getImageUrl());
    }


    private void logout() {
        Intent intent = new Intent(requireContext(), LoginPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();

    }
}