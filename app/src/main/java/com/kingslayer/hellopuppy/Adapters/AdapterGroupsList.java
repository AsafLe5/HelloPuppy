package com.kingslayer.hellopuppy.Adapters;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.kingslayer.hellopuppy.GroupProfile;
import com.kingslayer.hellopuppy.Models.ModelGroup;
import com.kingslayer.hellopuppy.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AdapterGroupsList extends RecyclerView.Adapter<AdapterGroupsList.HolderGroupsList>{

    private Context context;
    private ArrayList<ModelGroup> groupsChatList;
    private String groupId;
    private List<String> membersIds;

    public AdapterGroupsList(Context context, ArrayList<ModelGroup> groupsChatList){
        this.context = context;
        this.groupsChatList = groupsChatList;
        membersIds = new ArrayList<>();
//        membersIds.add("mask");
    }

    @NonNull
    @NotNull
    @Override
    public HolderGroupsList onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_group,
                parent, false);
        return new HolderGroupsList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HolderGroupsList holder, int position) {
        // get user data
        ModelGroup group = groupsChatList.get(position);

        String explanation = group.getExplanation();
        holder.explanation.setText(explanation);

        holder.setGroupId(group.getGroupId());

        String groupName = group.getGroupName();
        holder.groupName.setText(groupName);

        String walksPerWeek = group.getWalksPerWeek();
        holder.walksPerWeek.setText(walksPerWeek);

        String numOfMembers = group.getNumOfMembers();
        holder.numOfMembers.setText(numOfMembers);

        String pic = group.getGroupProfile();
        Picasso.get().load(pic).into(holder.groupPic);


        try {
//            Picasso.get().load().placeholder(R.drawable.ic_profile);
        }
        catch (Exception e){

        }

        // handle user click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getApplicationContext(), GroupProfile.class);
                intent.putExtra("GroupId", holder.getGroupId());
                context.startActivity(intent);
                //open group chat
//                Intent intent = new Intent(context, GroupChatActivity.class);
//                intent.putExtra("groupId", groupId);
//                context.startActivity(intent);
//                Toast.makeText(ModelUser.this,
//                        "user clicked!", Toast.LENGTH_SHORT).show();

            }
        });

        holder.itemView.findViewById(R.id.RequestJoin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                membersIds.add(FirebaseAuth.getInstance().getUid());
                FirebaseDatabase.getInstance().getReference().child("Groups")
                        .child(holder.getGroupId()).child("Join requests")
                        .setValue(membersIds);
            }
        });
    }


    @Override
    public int getItemCount() {
        return groupsChatList.size();
    }


    class HolderGroupsList extends RecyclerView.ViewHolder {

        private TextView walksPerWeek;
        private TextView numOfMembers;
        private TextView explanation;
        private TextView groupName;
        private ImageView groupPic;
        private String groupId;

        public HolderGroupsList(@NonNull @NotNull View itemView) {
            super(itemView);

            walksPerWeek = itemView.findViewById(R.id.walksPerWeek);
            numOfMembers = itemView.findViewById(R.id.NumOfMembers);
            explanation = itemView.findViewById(R.id.explanation_text);
            groupName = itemView.findViewById(R.id.groupsName);
            groupPic = itemView.findViewById(R.id.GroupPicture);
//            UserPicture = itemView.findViewById(R.id.UserPicture);
//            userName = itemView.findViewById(R.id.actualUserName);
//            dogName = itemView.findViewById(R.id.actualDogName);
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
    }
}
