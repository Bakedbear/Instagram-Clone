package com.ubunifu.appclone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ubunifu.appclone.R;
import com.ubunifu.appclone.models.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context mContext;
    ArrayList<User> mUserArrayList;

    public UserAdapter(Context mContext, ArrayList<User> mUserArrayList) {
        this.mContext = mContext;
        this.mUserArrayList = mUserArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item_list, parent, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = mUserArrayList.get(position);
        holder.fullname.setText(user.getFull_names());
        Glide.with(mContext).load(mUserArrayList.get(position).getProfile_img_url()).into(holder.profImage);
        holder.email.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return mUserArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        CircleImageView profImage;
        TextView fullname, email;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profImage = itemView.findViewById(R.id.search_profile_img);
            fullname = itemView.findViewById(R.id.UserFullnames);
            email = itemView.findViewById(R.id.Userfull_email);

        }
    }
}
