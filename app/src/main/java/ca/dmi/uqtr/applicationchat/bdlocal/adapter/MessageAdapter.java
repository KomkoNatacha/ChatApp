package ca.dmi.uqtr.applicationchat.bdlocal.adapter;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import java.util.List;


import ca.dmi.uqtr.applicationchat.R;
import ca.dmi.uqtr.applicationchat.bdlocal.model.Message;
import ca.dmi.uqtr.applicationchat.databinding.ItemAudioReceiveMessageBinding;
import ca.dmi.uqtr.applicationchat.databinding.ItemAudioSendMessageBinding;
import ca.dmi.uqtr.applicationchat.databinding.ItemReceiveImageLayoutBinding;
import ca.dmi.uqtr.applicationchat.databinding.ItemSendImageLayoutBinding;
import ca.dmi.uqtr.applicationchat.databinding.ItemSendMessageBinding;
import ca.dmi.uqtr.applicationchat.databinding.ItemReceiveMessageBinding;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList;

    private static final int TYPE_SEND_TEXT_MESSAGE = 01;
    private static final int TYPE_RECEIVE_TEXT_MESSAGE = 02;
    private static final int TYPE_SEND_IMAGE_MESSAGE = 10;
    private static final int TYPE_RECEIVE_IMAGE_MESSAGE = 12;
    private static final int TYPE_SEND_AUDIO_MESSAGE = 20;
    private static final int TYPE_RECEIVE_AUDIO_MESSAGE = 22;
    private MediaPlayer mediaPlayer;

    private Context context;

    // ID de l'utilisateur actuellement connecté
    private final String currentUserId;

    public MessageAdapter(List<Message> messageList, Context context, String currentUserId) {
        this.messageList = messageList;
        this.context = context;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);

        if (currentUserId.equals(message.getSenderId())) {
            if (message.getImageUrl() != null) {
                return TYPE_SEND_IMAGE_MESSAGE;
            } else if (message.getAudioUrl() != null) {
                return TYPE_SEND_AUDIO_MESSAGE;
            } else {
                return TYPE_SEND_TEXT_MESSAGE;
            }
        } else {
            if (message.getImageUrl() != null) {
                return TYPE_RECEIVE_IMAGE_MESSAGE;
            } else if (message.getAudioUrl() != null) {
                return TYPE_RECEIVE_AUDIO_MESSAGE;
            } else {
                return TYPE_RECEIVE_TEXT_MESSAGE;
            }
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_SEND_TEXT_MESSAGE) {
            ItemSendMessageBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_send_message, parent, false);
            return new SendMessageViewHolder(binding);
        } else if (viewType == TYPE_RECEIVE_TEXT_MESSAGE) {
            ItemReceiveMessageBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_receive_message, parent, false);
            return new ReceiveMessageViewHolder(binding);
        } else if (viewType == TYPE_SEND_IMAGE_MESSAGE) {
            ItemSendImageLayoutBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_send_image_layout, parent, false);
            return new SendImageMessageViewHolder(binding);
        } else if (viewType == TYPE_RECEIVE_IMAGE_MESSAGE) {
            ItemReceiveImageLayoutBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_receive_image_layout, parent, false);
            return new ReceiveImageMessageViewHolder(binding);
        } else if (viewType == TYPE_SEND_AUDIO_MESSAGE) {
            ItemAudioSendMessageBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_audio__send_message, parent, false);
            return new SendAudioMessageViewHolder(binding);
        } else if (viewType == TYPE_RECEIVE_AUDIO_MESSAGE) {
            ItemAudioReceiveMessageBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_audio_receive_message, parent, false);
            return new ReceiveAudioMessageViewHolder(binding);
        }

        throw new IllegalArgumentException("Invalid view type: " + viewType);
    }

    public void setData(List<Message> messages) {
        if (messages != null && !messages.isEmpty()) {
            this.messageList = messages;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (holder instanceof SendMessageViewHolder) {
            ((SendMessageViewHolder) holder).bind(message);
        } else if (holder instanceof ReceiveMessageViewHolder) {
            ((ReceiveMessageViewHolder) holder).bind(message);
        } else if (holder instanceof SendImageMessageViewHolder) {
            ((SendImageMessageViewHolder) holder).bind(message);
        } else if (holder instanceof ReceiveImageMessageViewHolder) {
            ((ReceiveImageMessageViewHolder) holder).bind(message);
        }
        if (holder instanceof SendAudioMessageViewHolder) {
            SendAudioMessageViewHolder sendAudioHolder = (SendAudioMessageViewHolder) holder;
            sendAudioHolder.audioIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String audioFilePath = message.getAudioUrl();
                    toggleAudio(audioFilePath);



                }
            });
        } else if (holder instanceof ReceiveAudioMessageViewHolder) {
            ReceiveAudioMessageViewHolder receiveAudioHolder = (ReceiveAudioMessageViewHolder) holder;
            receiveAudioHolder.audioIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String audioFilePath = message.getAudioUrl();
                    toggleAudio(audioFilePath);

                }

            });
        }


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
    public static class SendMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemSendMessageBinding binding;

        public SendMessageViewHolder(ItemSendMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Message message) {
            binding.setMessage(message);
            binding.executePendingBindings();
        }
    }

    public static class ReceiveMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemReceiveMessageBinding binding;

        public ReceiveMessageViewHolder(ItemReceiveMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Message message) {
            binding.setMessage(message);
            binding.executePendingBindings();
        }
    }


    public static class SendImageMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemSendImageLayoutBinding binding;

        public SendImageMessageViewHolder(ItemSendImageLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Message message) {
            binding.setMessage(message);
            binding.executePendingBindings();
            if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(message.getImageUrl())
                        .into(binding.imageView);
            } else {
            }
        }
    }

    public static class ReceiveImageMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemReceiveImageLayoutBinding binding;

        public ReceiveImageMessageViewHolder(ItemReceiveImageLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Message message) {
            binding.setMessage(message);
            binding.executePendingBindings();
            if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext()).load(message.getImageUrl()).into(binding.imageView);
            } else {
            }
        }
    }

    public static class SendAudioMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemAudioSendMessageBinding binding;
        public ImageView audioIcon;


        public SendAudioMessageViewHolder(ItemAudioSendMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.audioIcon = binding.audioIcon;
        }

        public void bind(Message message) {
            binding.setMessage(message);
            binding.executePendingBindings();
            if (message.getAudioUrl() != null && !message.getAudioUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(R.drawable.ic_microblanc)
                        .into(binding.audioIcon);
            } else {

            }
        }
    }

    public static class ReceiveAudioMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemAudioReceiveMessageBinding binding;
        public ImageView audioIcon;

        public ReceiveAudioMessageViewHolder(ItemAudioReceiveMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.audioIcon = binding.audioIcon;
        }

        public void bind(Message message) {
            binding.setMessage(message);
            binding.executePendingBindings();
            if (message.getAudioUrl() != null && !message.getAudioUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(R.drawable.ic_microblanc)
                        .into(binding.audioIcon);
            } else {

            }


        }
    }

    // Méthode pour lire l'audio
    private void playAudio(String audioFilePath) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioFilePath);

                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());

                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();

                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopAudio();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    // Méthode pour arrêter la lecture audio
    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();

            mediaPlayer = null;

        }

    }
    // Méthode pour gérer la lecture/arrêt de l'audio
    private void toggleAudio(String audioFilePath) {
        if (mediaPlayer == null) {
            playAudio(audioFilePath);
        } else {
            stopAudio();
        }
    }

}
