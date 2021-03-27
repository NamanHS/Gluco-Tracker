package tech.hans.glucotracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class readArticles extends AppCompatActivity {

    RecyclerView firestoreList;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<Articles, ArticlesViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_articles);

        firestoreList = (RecyclerView) findViewById(R.id.firestore_list);
        firebaseFirestore = FirebaseFirestore.getInstance();

        //QUERY
        Query query = firebaseFirestore.collection("ArticlesDB").orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Articles> options = new FirestoreRecyclerOptions.Builder<Articles>()
                .setQuery(query, Articles.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Articles, ArticlesViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull ArticlesViewHolder holder, int position, @NonNull Articles model) {
                String showDate = model.getDate().toString();
                String showTitle = model.getTitle();
                String showUrl = model.getUrl();
                holder.articleTitle.setText(showTitle);
            }

            @NonNull
            @Override
            public ArticlesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single_article, parent, false);
                return new ArticlesViewHolder(view);
            }

        };

        firestoreList.setHasFixedSize(true);
        firestoreList.setLayoutManager(new LinearLayoutManager(this));
        firestoreList.setAdapter(adapter);

        //visit link
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction){

                new AlertDialog.Builder(readArticles.this).setCancelable(false)
                        .setTitle("ARE YOU SURE YOU WANT TO VISIT ?")
                        .setPositiveButton("VISIT", new DialogInterface.OnClickListener() {
                            @SuppressLint("SetJavaScriptEnabled")
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Articles itemToVisit = adapter.getItem(viewHolder.getAdapterPosition());
                                String url = itemToVisit.getUrl();
                                Intent intent = new Intent(readArticles.this,WebArticles.class);
                                intent.putExtra("url",url);
                                startActivity(intent);



                            }

                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            }
                        })
                        .create()
                        .show();
            }
        }).attachToRecyclerView(firestoreList);

    }

        private class ArticlesViewHolder extends RecyclerView.ViewHolder {

            private TextView articleTitle;

            public ArticlesViewHolder(@NonNull View itemView) {
                super(itemView);
                articleTitle = itemView.findViewById(R.id.articleTitle);
            }
        }

    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }
}