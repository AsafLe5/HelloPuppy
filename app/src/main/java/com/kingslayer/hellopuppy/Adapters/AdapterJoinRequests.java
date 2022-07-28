package com.kingslayer.hellopuppy.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kingslayer.hellopuppy.Constants;
import com.kingslayer.hellopuppy.Group;
import com.kingslayer.hellopuppy.Models.ModelUser;
import com.kingslayer.hellopuppy.R;
import com.kingslayer.hellopuppy.WatchProfile;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AdapterJoinRequests extends
        RecyclerView.Adapter<AdapterJoinRequests.HolderJoinRequestsList> {

    private Context context;
    private ArrayList<ModelUser> usersChatList;
    private String groupId;
    private List<String> usersInGroup;
    private List<String> requestsIds;
    public AdapterJoinRequests(Context context, ArrayList<ModelUser> usersChatList,
                               String groupId,List<String> usersInGroup, List<String> requestsIds){
        this.context = context;
        this.usersChatList = usersChatList;
        this.groupId = groupId;
        this.usersInGroup = usersInGroup;
        this.requestsIds = requestsIds;
    }

    @NonNull
    @NotNull
    @Override
    public AdapterJoinRequests.HolderJoinRequestsList onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_join_request,
                parent, false);
        return new HolderJoinRequestsList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HolderJoinRequestsList holder, int position) {
        // get user data
        ModelUser user = usersChatList.get(position);
        holder.setUserId(user.getUserId());
        setPicFromDB(user.getUserProfile(), holder);
        // get the data of the user- his name and the dogs name
        String userName = user.getUserName();
        String availability = user.getAvailability();
        String[] a = availability.split(" ");
        availability = a[0] + " " +a[1];
        //groupId = user.getGroupId();
        holder.availability.setText(availability);
        holder.userName.setText(userName);


        holder.itemView.findViewById(R.id.accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // user_id, group_id
                ModelUser user = usersChatList.get(position);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                reference.child("Users").child(user.getUserId())
                        .child(Constants.GROUP_ID_DB).setValue(groupId);

                usersInGroup.add(user.getUserId().toString());
                reference.child("Groups").child(groupId).child("MembersIds").setValue(usersInGroup);

                usersChatList.remove(user);
                requestsIds.remove(user.getUserId());

                DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                DbRef.child("Join requests").setValue(requestsIds);

                FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                        .child("ScheduleChoices").child(user.getUserId().toString()).child("Credits").setValue(5);
            }
        });

        holder.itemView.findViewById(R.id.deny).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelUser user = usersChatList.get(position);
                usersChatList.remove(user);

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WatchProfile.class);
                intent.putExtra("User", holder.getUserId());
                context.startActivity(intent);
            }
        });
    }

    private void setPicFromDB(String urlUser, HolderJoinRequestsList holder){

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference= storageReference.child("User profile/"
                + holder.userId);

        final long TEN_MEGABYTE = 1024 * 1024;
        photoReference.getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.UserPicture.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if(urlUser!=null){
                    Picasso.get().load(urlUser).into(holder.UserPicture);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersChatList.size();
    }

    class HolderJoinRequestsList extends RecyclerView.ViewHolder {

        private ImageView UserPicture;
        private TextView userName;
        private TextView availability;
        private String userId;

        public HolderJoinRequestsList(@NonNull @NotNull View itemView) {
            super(itemView);
//            UserPicture = itemView.findViewById(R.id.UserPicture);
            userName = itemView.findViewById(R.id.actualUserName);
            availability = itemView.findViewById(R.id.actualAvailability);
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
