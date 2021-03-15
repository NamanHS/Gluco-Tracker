package tech.hans.glucotracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class readArticles extends AppCompatActivity {

    private GlucoReading temp;
    private String tempID;
    private RecyclerView firestoreList;
    private FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<GlucoReading, viewReading.GlucoReadingViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_articles);

        firestoreList = (RecyclerView) findViewById(R.id.firestore_list);
    }
}