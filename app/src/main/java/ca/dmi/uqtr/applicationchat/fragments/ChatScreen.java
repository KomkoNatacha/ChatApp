package ca.dmi.uqtr.applicationchat.fragments;

import static android.app.Activity.RESULT_OK;

import static com.google.firebase.messaging.Constants.TAG;
import static java.util.Objects.requireNonNull;

import android.content.ComponentName;
import android.content.Context;

import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;

import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

import android.content.Intent;

import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.text.Editable;
import android.text.TextWatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


import ca.dmi.uqtr.applicationchat.AudioRecorderUtil;
import ca.dmi.uqtr.applicationchat.MainPage;

import ca.dmi.uqtr.applicationchat.R;
import ca.dmi.uqtr.applicationchat.bdlocal.App;
import ca.dmi.uqtr.applicationchat.bdlocal.adapter.MessageAdapter;

import ca.dmi.uqtr.applicationchat.bdlocal.model.Message;


import ca.dmi.uqtr.applicationchat.databinding.ChatScreenBinding;
import ca.dmi.uqtr.applicationchat.preferences.UserPreferences;


import ca.dmi.uqtr.applicationchat.services.ApiService;
import ca.dmi.uqtr.applicationchat.services.FcmService;
import ca.dmi.uqtr.applicationchat.services.dto.FcmMessageDTO;
import ca.dmi.uqtr.applicationchat.viewmodels.ContactViewModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatScreen extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ARG_USER_ID = "user_id";
    private static final String ARG_FIRST_NAME = "first_name";
    private static final String ARG_USER_PHOTO_URL = "photo_url";

    Message message;
    private ApiService apiService;
    private static final int RECORD_AUDIO_PERMISSION_CODE = 8;
    private static final int MESSAGE_TYPE_IMAGE = 1;
    private static final int MESSAGE_TYPE_AUDIO = 2;
    private ChatScreenBinding binding;
    private MessageAdapter messageAdapter;
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 123;

    private List<Message> messageList;
    private boolean isRecording = false;
    private String audioFilePath;

    private CardView imagePickerLayout;

    private String currentPhotoPath;

    private FcmService fcmService;
    private UserPreferences userPreferences;

    private EditText editTextMessage;

    private File currentImageFile;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleCameraResult
    );

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleGalleryResult
    );


    // TODO: Rename and change types of parameters
    private String userId;
    private String firstName;
    private String photoUrl;

    public ChatScreen() {

    }


    public static ChatScreen newInstance(String userId, String firstName, String photoUrl) {
        ChatScreen fragment = new ChatScreen();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        args.putString(ARG_FIRST_NAME, firstName);
        args.putString(ARG_USER_PHOTO_URL, photoUrl);
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
            userId = getArguments().getString(ARG_USER_ID);
            firstName = getArguments().getString(ARG_FIRST_NAME);
            photoUrl = getArguments().getString(ARG_USER_PHOTO_URL);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        userPreferences = new UserPreferences(requireContext());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.chat_screen, container, false);
        ContactViewModel contactViewModel = new ViewModelProvider(requireActivity()).get(ContactViewModel.class);
        binding.setUsernam(contactViewModel);
        fcmService = FcmService.getInstance();
        View view = binding.getRoot();
        imagePickerLayout = view.findViewById(R.id.imagePickerLayout);
        editTextMessage = binding.inputMess;


        contactViewModel.getSelectedUserNameLiveData().observe(getViewLifecycleOwner(), firstName -> {
            TextView textViewName = binding.nameUs;
            textViewName.setText(firstName);
        });


        contactViewModel.getSelectedUserProfilePhotoLiveData().observe(getViewLifecycleOwner(), userProfilePhoto -> {
            ImageView userImageView = binding.imageProfile;
            Glide.with(this)
                    .load(userProfilePhoto)
                    .placeholder(R.drawable.baseline_android_24)  // Image par défaut
                    .into(userImageView);
        });


        String currentUserId = userPreferences.getUserId();


        messageList = new ArrayList<>();


        messageAdapter = new MessageAdapter(messageList, requireContext(), currentUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());


        binding.chatList.setLayoutManager(layoutManager);
        binding.chatList.scrollToPosition(messageList.size() - 1);

        binding.chatList.setAdapter(messageAdapter);

        Bundle arguments = getArguments();
        String contactId = arguments.getString(ARG_USER_ID);


        binding.layoutSend.setOnClickListener(v -> sendMessageText(message));
        binding.imageBackChat.setOnClickListener(v -> openContact());
        binding.setLifecycleOwner(getViewLifecycleOwner());


        EditText inputMess = binding.inputMess;
        AppCompatImageView iconAudio = binding.iconAudio;
        AppCompatImageView iconAttachment = binding.iconAttachment;
        FrameLayout layoutSend = binding.layoutSend;


        inputMess.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Après que le texte a changé, ajuste la visibilité de layoutSend
                layoutSend.setVisibility(editable.toString().isEmpty() ? View.GONE : View.VISIBLE);
                iconAudio.setVisibility(editable.toString().isEmpty() ? View.VISIBLE : View.INVISIBLE);
                iconAttachment.setVisibility(editable.toString().isEmpty() ? View.VISIBLE : View.INVISIBLE);
            }
        });

        //  OnClickListener pour le bouton d'envoi
        layoutSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageText(message);

            }
        });

        inputMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lorsque l'EditText est cliqué, affiche le layoutSend
                layoutSend.setVisibility(View.VISIBLE);
            }
        });

        binding.imageBackChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContact();
            }
        });


        iconAttachment.setOnClickListener(v -> {
            // Change la visibilité du layout image_picker
            imagePickerLayout.setVisibility(View.VISIBLE);

        });

        //Faire disparaitre le cardview quand on clique n importe ou sur l ecran
        AppCompatImageView closePickerImageView = view.findViewById(R.id.close_picker);

        closePickerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change la visibilité du layout image_picker à GONE
                imagePickerLayout.setVisibility(View.GONE);
            }
        });


        File externalFilesDir = requireContext().getExternalFilesDir(null);
        if (externalFilesDir != null) {
            audioFilePath = externalFilesDir.getAbsolutePath() + "/audio_record.3gp";
        } else {
            Toast.makeText(requireContext(), "Le répertoire externe n'est pas disponible.", Toast.LENGTH_SHORT).show();

        }
        checkPermission();
        iconAudio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!isRecording) {
                    startRecording(audioFilePath);

                } else {
                    stopRecording();
                    sendMessageAudio(audioFilePath);
                }
            }
        });


        Button takePictButton = imagePickerLayout.findViewById(R.id.take_pict);
        Button choosePictButton = imagePickerLayout.findViewById(R.id.choose_pict);
        choosePictButton.setOnClickListener(v -> openGallery());
        takePictButton.setOnClickListener(v -> openCamera());
        return view;
    }

    //Envoi de message text
    private void sendMessageText(Message message) {
        sendTextMessageViaFCM();
        editTextMessage.setText("");

    }

    // Méthode pour envoyer un message texte
    private void sendTextMessageViaFCM() {
        String textMessage = binding.inputMess.getText().toString().trim();
        if (!textMessage.isEmpty()) {

            Bundle arguments = getArguments();
            String contactId = arguments.getString(ARG_USER_ID);
            String userNam = arguments.getString(ARG_FIRST_NAME);
            String currentUserId = userPreferences.getUserId();

            Message message = new Message(textMessage, new Date(), contactId, currentUserId);

            FcmMessageDTO fcmMessageDTO = new FcmMessageDTO(message, FcmMessageDTO.TYPE_TEXT);
            fcmService.sendTextMessageViaFCM(fcmMessageDTO, new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        requireActivity().runOnUiThread(() -> {
                            sendToGlitch(message);

                        });

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

        }
    }


    //Ouvrir la camera
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            try {
                currentImageFile = createImageFile();
                Uri photoUri = FileProvider.getUriForFile(requireContext(),
                        "ca.dmi.uqtr.applicationchat.fileprovider", currentImageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                cameraLauncher.launch(takePictureIntent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleCameraResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            displaySelectedImage(Uri.fromFile(new File(currentPhotoPath)));
        }
    }

    private void displaySelectedImage(Uri imageUri) {
        // Convertir l'URI en fichier
        File selectedImageFile = new File(requireNonNull(imageUri.getPath()));
        Bundle arguments = getArguments();
        String contactId = arguments.getString(ARG_USER_ID);
        Message imageMessage = new Message("", new Date(), contactId, userPreferences.getUserId());
        imageMessage.setType(MESSAGE_TYPE_IMAGE);
        sendImageMessage(imageMessage, imageUri);
        binding.selectedImageView.setVisibility(View.GONE);
    }

    private File createImageFile() throws IOException {
        String imageFileName = UUID.randomUUID().toString();
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    //Ouvrir la gallery
    private void openGallery() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        }
        galleryActivityResultLauncher.launch(new PickVisualMediaRequest());
    }


    ActivityResultLauncher<PickVisualMediaRequest> galleryActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(),
                    this::displaySelectedImage);


    private void handleGalleryResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            Uri selectedImageUri = data.getData();
            String selectedImagePath = getPathFromUri(requireContext(), selectedImageUri);
            File selectedImageFile = new File(selectedImagePath);
            displaySelectedImage(selectedImageUri);
        }


    }

    public static String getPathFromUri(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;

        try {
            if ("file".equals(uri.getScheme())) {
                return uri.getPath();
            }
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    //Envoi de message de type image
    private void sendImageMessage(Message message, Uri imageUri) {
        String imagePath = getPathFromUri(requireContext(), imageUri);

        if (imagePath != null) {
            File imageFile = new File(imagePath);

            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

            apiService.uploadImage(filePart, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        message.setImageUrl(apiService.getImageUrl());
                        sendToGlitch(message);

                    } else {

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }
            });
        }
    }

    //Envoi de message de type audio
    private void startRecording(String audioFilePath) {
        AudioRecorderUtil.startRecording(audioFilePath);
        isRecording = true;
    }

    private void stopRecording() {
        AudioRecorderUtil.stopRecording();
        isRecording = false;
    }

    private void checkPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_CODE);

    }


    private void sendMessageAudio(String audioFilePath) {
        MultipartBody.Part audioFilePart = prepareAudioPart(audioFilePath);
        Message audioMessage = new Message("", new Date(), userId, userPreferences.getUserId());
        audioMessage.setType(MESSAGE_TYPE_AUDIO);
        audioMessage.setAudioUrl(audioFilePath);
        sendAudioMessage(audioMessage, audioFilePart);

    }


    private void sendAudioMessage(Message message, MultipartBody.Part audio) {
        apiService.uploadAudio(audioFilePath, audio, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String audioUrl = response.body().string();
                        message.setAudioUrl(apiService.getAudioUrl());

                        sendToGlitch(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    private MultipartBody.Part prepareAudioPart(String audioFilePath) {
        File file = new File(audioFilePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/*"), file);
        return MultipartBody.Part.createFormData("audio", file.getName(), requestFile);
    }


    //Envoi de message a glitch
    private void sendToGlitch(Message message) {
        if (apiService != null) {
            apiService.sendMessage(message, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        messageList.add(message);
                        int newPosition = messageList.size() - 1;
                        messageAdapter.notifyItemInserted(newPosition);
                        binding.chatList.scrollToPosition(newPosition);

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }
            });
        }
    }


    //Recuperer le message de glitch
    private void retrieveMessagesFromGlitch() {
        if (apiService != null) {
            String currentUserId = userPreferences.getUserId();
            Bundle arguments = getArguments();
            String contactId = arguments.getString(ARG_USER_ID);

            apiService.getMessages(contactId, currentUserId, new Callback<List<Message>>() {
                @Override
                public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                    if (response.isSuccessful()) {
                        List<Message> glitchMessages = response.body();
                        if (glitchMessages != null && !glitchMessages.isEmpty()) {
                            for (Message message : glitchMessages) {
                                messageList.add(message);
                            }
                            messageAdapter.notifyDataSetChanged();
                            binding.chatList.scrollToPosition(messageList.size() - 1);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Message>> call, Throwable t) {
                }
            });
        }
    }


    private void openContact() {
        Intent intent = new Intent(requireContext(), MainPage.class);
        startActivity(intent);
        requireActivity().finish();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
    }

    private final ServiceConnection apiServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ApiService.LocalBinder binder = (ApiService.LocalBinder) iBinder;
            apiService = binder.getService();
            retrieveMessagesFromGlitch();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            apiService = null;
        }
    };




}



