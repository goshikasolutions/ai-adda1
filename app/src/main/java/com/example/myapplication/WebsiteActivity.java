package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WebsiteActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private List<String> WebsiteList;
    private RecyclerView recyclerViewWebsite;

    private WebsiteListAdapter websiteListAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("category");

        WebsiteList = new ArrayList<>();

        recyclerViewWebsite = findViewById(R.id.recyclerViewWebsiteList);
        recyclerViewWebsite.setLayoutManager(new LinearLayoutManager(this));
        websiteListAdapter = new WebsiteListAdapter(WebsiteList);
        recyclerViewWebsite.setAdapter(websiteListAdapter);

        mDatabase.child("websites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TAG", "Document ID: ");
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    String chatItem = chatSnapshot.getValue(String.class);
                    Log.d("TAG", "Document ID: " + chatItem);
                    WebsiteList.add(chatItem);
                }
                websiteListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    public class WebsiteListAdapter extends RecyclerView.Adapter<WebsiteListAdapter.ViewHolder> {
        private List<String> chatList;

        public WebsiteListAdapter(List<String> chatList) {
            this.chatList = chatList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.website_list, parent, false);
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
                chatTextView = itemView.findViewById(R.id.textViewWebsiteName);
            }
        }
    }
}