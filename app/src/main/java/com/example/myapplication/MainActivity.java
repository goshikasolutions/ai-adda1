package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private List<String> chatList;
    private RecyclerView recyclerView;
    private ChatListAdapter chatListAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("category");

        chatList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatListAdapter = new ChatListAdapter(chatList);
        recyclerView.setAdapter(chatListAdapter);

        mDatabase.child("list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TAG", "Document ID: ");
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    String chatItem = chatSnapshot.getValue(String.class);
                    Log.d("TAG", "Document ID: " + chatItem);
                    chatList.add(chatItem);
                }
                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
        private List<String> chatList;

        public ChatListAdapter(List<String> chatList) {
            this.chatList = chatList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String chatItem = chatList.get(position);
            holder.chatTextView.setText(chatItem);
        }

        @Override
        public int getItemCount() {
            return chatList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView chatTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                chatTextView = itemView.findViewById(R.id.textViewUsername);
            }
        }
    }
}