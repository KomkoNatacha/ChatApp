package ca.dmi.uqtr.applicationchat.bdlocal.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import java.util.List;

import ca.dmi.uqtr.applicationchat.R;
import ca.dmi.uqtr.applicationchat.bdlocal.model.UserInfo;
import ca.dmi.uqtr.applicationchat.databinding.UserItemBinding;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<UserInfo> userList;
    private UserClickListener userClickListener;

    public interface UserClickListener {
        void onUserClick(UserInfo user);
    }

    public UserAdapter(List<UserInfo> userList, UserClickListener userClickListener) {
        this.userList = userList;
        this.userClickListener = userClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        UserItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.user_item, parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserInfo user = userList.get(position);
        holder.bind(user);

        Glide.with(holder.userImage.getContext())
                .load(user.getImageUrl())
                .placeholder(R.drawable.baseline_android_24)
                .into(holder.userImage);

        holder.itemView.setOnClickListener(v -> {
            if (userClickListener != null) {
                userClickListener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public void setUserList(List<UserInfo> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        private final UserItemBinding binding;
        ImageView userImage;

        public UserViewHolder(UserItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            userImage = binding.imageProfile;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && userClickListener != null) {
                    userClickListener.onUserClick(userList.get(position));
                }
            });
        }

        public void bind(UserInfo user) {
            binding.setUser(user);
            binding.executePendingBindings();
        }
    }
}
