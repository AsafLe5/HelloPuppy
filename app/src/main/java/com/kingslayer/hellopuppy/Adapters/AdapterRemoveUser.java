package com.kingslayer.hellopuppy.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kingslayer.hellopuppy.Models.ModelUser;
import com.kingslayer.hellopuppy.R;
import com.kingslayer.hellopuppy.WatchProfile;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kingslayer.hellopuppy.Models.ModelUser;
import com.kingslayer.hellopuppy.R;
import com.kingslayer.hellopuppy.WatchProfile;
import com.squareup.picasso.Picasso;
import com.kingslayer.hellopuppy.Group;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AdapterRemoveUser extends RecyclerView.Adapter<AdapterRemoveUser.HolderRemoveUserList>{

    private Context context;
    private ArrayList<ModelUser> usersChatList;
    private String userId;

    public AdapterRemoveUser(Context context, ArrayList<ModelUser> usersChatList){
        this.context = context;
        this.usersChatList = usersChatList;
    }

    @NonNull
    @NotNull
    @Override
    public HolderRemoveUserList onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_remove_user,
                parent, false);
        return new HolderRemoveUserList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HolderRemoveUserList holder, int position) {
        // get user data
        ModelUser user = usersChatList.get(position);
        // get the data of the user- his name and the dogs name
        String userName = user.getUserName();
        String dogsName = user.getDogsName();
//        userId = user.getUserId();
        holder.setUserId(user.getUserId());
        holder.dogName.setText(dogsName);
        holder.userName.setText(userName);

        Picasso.get().load(user.getUserProfile()).into(holder.UserPicture);

        int a = 8;
        // handle user click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(ModelUser.this,
//                        "user clicked!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), WatchProfile.class);
                intent.putExtra("User", holder.getUserId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersChatList.size();
    }


    class HolderRemoveUserList extends RecyclerView.ViewHolder {

        private ImageView UserPicture;
        private TextView userName;
        private TextView dogName;
        private String userId;

        public HolderRemoveUserList(@NonNull @NotNull View itemView) {
            super(itemView);
//            UserPicture = itemView.findViewById(R.id.UserPicture);
            userName = itemView.findViewById(R.id.actualUserName);
            dogName = itemView.findViewById(R.id.actualDogName);
            UserPicture = itemView.findViewById(R.id.UserPicture);
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }
    }
}
