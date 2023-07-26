package com.goshika.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

public class WebsiteActivity extends AppCompatActivity {

    private List<CategoryItem> webList;
    private RecyclerView recyclerView;
    private WebsiteListAdapter websiteListAdapter;
    private CollectionReference categoryRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);
        String Type = getIntent().getStringExtra("Type");
//
//        Toolbar toolbar1 = findViewById(R.id.toolbar2);
//        setSupportActionBar(toolbar1);
//
//        // Set the subtitle in the toolbar
//        getSupportActionBar().setSubtitle("Your Subtitle");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        categoryRef = db.collection("category");
        webList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewWebsiteList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        websiteListAdapter = new WebsiteListAdapter(webList);
        recyclerView.setAdapter(websiteListAdapter);

        categoryRef.document(Type).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    List<Map<String, String>> items = (List<Map<String, String>>) data.get("items");

                    for (Map<String, String> item : items) {
                        String name = item.get("name");
                        String icon = item.get("icon");
                        String link = item.get("link");
                        webList.add(new CategoryItem(name, icon, link));;
                    }

                    websiteListAdapter.notifyDataSetChanged();
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

    public class WebsiteListAdapter extends RecyclerView.Adapter<WebsiteListAdapter.ViewHolder> {
        private List<CategoryItem> chatList;

        public WebsiteListAdapter(List<CategoryItem> chatList) {
            this.chatList = chatList;
        }

        @NonNull
        @Override
        public WebsiteListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.website_list, parent, false);
            return new WebsiteListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WebsiteListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            CategoryItem chatItem = chatList.get(position);
            String name = chatItem.getName();
            String icon = chatItem.getIcon();
            String link = chatItem.getLink();
            holder.name.setText(name);
            holder.link.setText(link);
            RequestOptions requestOptions = new RequestOptions()
                    .error(R.drawable.ic_menu_camera); // Error image if the loading fails

            Glide.with(holder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(icon)
                    .into(holder.icon);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open the link in a browser
                    Toast.makeText(v.getContext(), "Thanks for using AI Adda ", Toast.LENGTH_SHORT).show();
                    Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(urlIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return chatList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public TextView link;
            public ImageView icon;

            public ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.textViewWebsiteName);
                link = itemView.findViewById(R.id.textViewWebsiteLink);
                icon = itemView.findViewById(R.id.imageViewProfile1);
            }
        }
    }

    public class CategoryItem {
        private String name;
        private String icon;
        private String link;

        public CategoryItem(String name, String icon, String link) {
            this.name = name;
            this.icon = icon;
            this.link = link;
        }

        public String getName() {
            return name;
        }

        public String getIcon() {
            return icon;
        }

        public String getLink() {
            return link;
        }
    }
}