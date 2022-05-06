package com.kingslayer.hellopuppy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kingslayer.hellopuppy.Models.ModelGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AdapterGroupsList extends RecyclerView.Adapter<AdapterGroupsList.HolderGroupsList>{

    private Context context;
    private ArrayList<ModelGroup> groupsChatList;
    private String groupId;

    public AdapterGroupsList(Context context, ArrayList<ModelGroup> groupsChatList){
        this.context = context;
        this.groupsChatList = groupsChatList;
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

        groupId = group.getGroupId();

        String groupName = group.getGroupName();
        holder.groupName.setText(groupName);

        String walksPerWeek = group.getWalksPerWeek();
        holder.walksPerWeek.setText(walksPerWeek);

        String numOfMembers = group.getNumOfMembers();
        holder.numOfMembers.setText(numOfMembers);


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
                intent.putExtra("GroupId", groupId);
                context.startActivity(intent);
                //open group chat
//                Intent intent = new Intent(context, GroupChatActivity.class);
//                intent.putExtra("groupId", groupId);
//                context.startActivity(intent);
//                Toast.makeText(ModelUser.this,
//                        "user clicked!", Toast.LENGTH_SHORT).show();

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
    }
}
