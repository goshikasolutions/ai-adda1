package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List<CategoryItem> chatList;
    private RecyclerView recyclerView;
    private ChatListAdapter chatListAdapter;
    private CollectionReference categoryRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        categoryRef = db.collection("category");
        chatList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatListAdapter = new ChatListAdapter(chatList);
        recyclerView.setAdapter(chatListAdapter);

        categoryRef.document("category_list").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    List<Map<String, String>> items = (List<Map<String, String>>) data.get("items");

                    for (Map<String, String> item : items) {
                        String type = item.get("type");
                        String imageURL = item.get("imageURL");
                        chatList.add(new CategoryItem(type, imageURL));;
                    }

                    chatListAdapter.notifyDataSetChanged();
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firestore", "Error getting data from ", e);
            }
        });
    }
    public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
        private List<CategoryItem> chatList;

        public ChatListAdapter(List<CategoryItem> chatList) {
            this.chatList = chatList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            CategoryItem chatItem = chatList.get(position);
            String type = chatItem.getType();
            String imageURL = chatItem.getImageURL();
            holder.chatTextView.setText(type);
            RequestOptions requestOptions = new RequestOptions()
                    .error(R.drawable.ic_menu_camera);

            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(imageURL)
                    .into(holder.image);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, WebsiteActivity.class);
                    intent.putExtra("Type", type);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return chatList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView chatTextView;
            public ImageView image;

            public ViewHolder(View itemView) {
                super(itemView);
                chatTextView = itemView.findViewById(R.id.textViewUsername);
                image = itemView.findViewById(R.id.imageViewProfile);
            }
        }
    }

    public class CategoryItem {
        private String type;
        private String imageURL;

        public CategoryItem(String type, String imageURL) {
            this.type = type;
            this.imageURL = imageURL;
        }

        public String getType() {
            return type;
        }

        public String getImageURL() {
            return imageURL;
        }
    }
}