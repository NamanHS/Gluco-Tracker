package tech.hans.glucotracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class viewReading extends AppCompatActivity {

    String regNo;
    private RecyclerView firestoreList;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter<GlucoReading, GlucoReadingViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reading);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        regNo = firebaseUser.getUid().toString();

        firestoreList = (RecyclerView) findViewById(R.id.firestore_list);

        firebaseFirestore = FirebaseFirestore.getInstance();

        //Query
        Query query = firebaseFirestore.collection("GlucoReadingDB").whereEqualTo("regNo",regNo).orderBy("date").orderBy("interval");

        FirestoreRecyclerOptions<GlucoReading> options = new FirestoreRecyclerOptions.Builder<GlucoReading>()
                .setQuery(query,GlucoReading.class)
                .build();

        adapter  = new FirestoreRecyclerAdapter<GlucoReading, GlucoReadingViewHolder>(options) {
            @NonNull
            @Override
            public GlucoReadingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single,parent,false);
                return new GlucoReadingViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull GlucoReadingViewHolder holder, int position, @NonNull GlucoReading model) {
                holder.reading_date.setText((model.getDate().toString()));
                holder.reading_measure.setText((Integer.toString(model.getGlucoReadingEntered())));
                holder.reading_interval.setText(model.getInterval());
                holder.reading_notes.setText(model.getOptionalNotes());

            }
        };
        firestoreList.setHasFixedSize(true);
        firestoreList.setLayoutManager(new LinearLayoutManager(this));
        firestoreList.setAdapter(adapter);
    }

    private class GlucoReadingViewHolder extends RecyclerView.ViewHolder {

        private TextView reading_date, reading_measure, reading_interval, reading_notes;

        public GlucoReadingViewHolder(@NonNull View itemView) {
            super(itemView);

            reading_date = itemView.findViewById(R.id.reading_date);
            reading_measure = itemView.findViewById(R.id.reading_measure);
            reading_interval = itemView.findViewById(R.id.reading_interval);
            reading_notes = itemView.findViewById(R.id.reading_notes);

        }
    }
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firestoreList.setAdapter(null);

    }
}