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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kingslayer.hellopuppy.Models.ModelUser;
import com.kingslayer.hellopuppy.R;
import com.kingslayer.hellopuppy.WatchProfile;

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
        // get the data of the user- his name and the dogs name
        String userName = user.getUserName();
        String availability = user.getAvailability();
        //groupId = user.getGroupId();
        holder.availability.setText(availability);
        holder.userName.setText(userName);

        holder.setUserId(user.getUserId());

        holder.itemView.findViewById(R.id.accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getUid().toString())
                        .child("GroupId").setValue(groupId);*/
                // user_id, group_id
                ModelUser user = usersChatList.get(position);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                reference.child("Users").child(user.getUserId())
                        .child("GroupId").setValue(groupId);


                usersInGroup.add(user.getUserId().toString());
                reference.child("Groups").child(groupId).child("MembersIds").setValue(usersInGroup);

                usersChatList.remove(user);
                requestsIds.remove(user.getUserId());

                DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
                DbRef.child("Join requests").setValue(requestsIds);

                FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                        .child("ScheduleChoices").child(user.getUserId().toString()).child("Credits").setValue(5);

//                DbRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
//                            if(postSnapshot.getValue().toString().equals(user.getUserId()))
//                            postSnapshot.
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//                    }
//                });


//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//                reference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                        for(DataSnapshot ds: snapshot.getChildren()){
//                            if (ds.child("Users").getKey().equals(FirebaseAuth.getInstance().getUid())){
//                                groupId = ds.child("GroupId").getValue().toString();
//                                reference.child("Users").child(user.getUserId())
//                                        .child("GroupId").setValue(groupId);
//
//                                List<String> allGroupMembers = (List<String>) ds.child("Groups")
//                                        .child(groupId).child("MembersIds");
//
//                                allGroupMembers.add(user.getUserId().toString());
//                                reference.child("Groups").child(groupId).child("MembersIds").setValue(allGroupMembers);
//
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//                    }
//                });
//                usersChatList.remove(user);
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
            availability = itemView.findViewById(R.id.Availability);
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }
    }
}
