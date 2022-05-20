package com.kingslayer.hellopuppy.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kingslayer.hellopuppy.Models.ModelGroup;
import com.kingslayer.hellopuppy.Models.ModelGroupChat;
import com.kingslayer.hellopuppy.R;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.HolderGroupChat>{

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private ArrayList<ModelGroupChat> chatArrayList;
    private FirebaseAuth firebaseAuth;

    public AdapterGroupChat(Context context, ArrayList<ModelGroupChat> chatArrayList){
        this.context = context;
        this.chatArrayList = chatArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @NotNull
    @Override
    public HolderGroupChat onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // inflate layouts
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_right,
                    parent, false);
            return new HolderGroupChat(view);
        }

        else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_left,
                    parent, false);
            return new HolderGroupChat(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HolderGroupChat holder, int position) {
        ModelGroupChat groupChat = chatArrayList.get(position);

        String message = groupChat.getMessage();
        holder.message.setText(message);

        String senderUid = groupChat.getSender();
        setUserName(groupChat, holder);


        String time = groupChat.getTimestamp();
        // convert timestamp to dd/mm/yy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(time));

        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.ENGLISH);
        String convertedTime = format1.format(calendar.getTime());

//        String convertedTime = DateFormat.getDateTimeInstance().format("dd/MM/yyyy hh:mm aa",calendar).toString();
        holder.time.setText(convertedTime);
    }

    private void setUserName(ModelGroupChat groupChat, HolderGroupChat holder) {
        // get the sender information from the uid.
        FirebaseDatabase.getInstance().getReference("Users").child(groupChat.getSender())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                for (DataSnapshot ds : snapshot.getChildren()){
                    String name = snapshot.child("Full name").getValue().toString();
                    holder.name.setText(name);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (chatArrayList.get(position).getSender().equals(firebaseAuth.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }

    class HolderGroupChat extends RecyclerView.ViewHolder {

        private TextView name, message, time;

        public HolderGroupChat(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }
    }
}


