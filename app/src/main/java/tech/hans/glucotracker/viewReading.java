package tech.hans.glucotracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class viewReading extends AppCompatActivity {

    private String regNo;
    private DocumentReference docToDelete;
    private GlucoReading temp;
    private String tempID;
    private RecyclerView firestoreList;
    private FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<GlucoReading, GlucoReadingViewHolder> adapter;

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reading);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        regNo = firebaseUser.getUid();

        firestoreList = (RecyclerView) findViewById(R.id.firestore_list);

        firebaseFirestore = FirebaseFirestore.getInstance();


        //Query
        Query query = firebaseFirestore.collection("GlucoReadingDB").whereEqualTo("regNo",regNo).orderBy("date", Query.Direction.DESCENDING).orderBy("interval");

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
                String showDate = model.getDate().toString();
                String finDate = showDate.substring(0,11) + showDate.substring(showDate.length()-5,showDate.length());
                holder.reading_date.setText((finDate));
                holder.reading_measure.setText((Integer.toString(model.getGlucoReadingEntered()))+" mg/dL");
                holder.reading_interval.setText(model.getInterval());
                if(!model.getOptionalNotes().equals("No Notes Added")){
                    holder.reading_notes.setVisibility(View.VISIBLE);
                    holder.reading_notes.setText(model.getOptionalNotes());

                }else{
                    holder.reading_notes.setVisibility(View.INVISIBLE);
                }

            }

            protected void deleteorUpdateItem(int position){
                docToDelete = getSnapshots().getSnapshot(position).getReference();

            }
        };
        firestoreList.setHasFixedSize(true);
        firestoreList.setLayoutManager(new LinearLayoutManager(this));
        firestoreList.setAdapter(adapter);


        //-----------------------------------------------------------DELETE GLUCOSE READING-----------------------------------------------------------------------

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction){

                new AlertDialog.Builder(viewReading.this).setCancelable(false)
                        .setTitle("ARE YOU SURE YOU WANT TO DELETE ?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                GlucoReading itemToDelete = adapter.getItem(viewHolder.getAdapterPosition());
                                Date date = itemToDelete.getDate();
                                Query query = firebaseFirestore.collection("GlucoReadingDB").whereEqualTo("regNo",regNo).whereEqualTo("date",date);
                                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    DocumentReference temp;
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for(QueryDocumentSnapshot querySnapshot : queryDocumentSnapshots){
                                            temp = querySnapshot.getReference();
                                        }
                                        temp.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mp = MediaPlayer.create(getApplicationContext(),getResources().getIdentifier("aknowledge", "raw", getPackageName()));
                                                mp.start();
                                                Toast toast = Toast.makeText(getApplicationContext(), "\nDELETED SUCCESSFULLY\n", Toast.LENGTH_SHORT);
                                                toast.setGravity(Gravity.CENTER,0,0);
                                                toast.show();
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                });

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


                //


            }

        }).attachToRecyclerView(firestoreList);


        //-----------------------------------------------------------FOR UPDATING-----------------------------------------------------------------
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction){

                new AlertDialog.Builder(viewReading.this).setCancelable(false)
                        .setTitle("ARE YOU SURE YOU WANT TO EDIT ?")
                        .setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                GlucoReading itemToDelete = adapter.getItem(viewHolder.getAdapterPosition());
                                Date date = itemToDelete.getDate();
                                Query query = firebaseFirestore.collection("GlucoReadingDB").whereEqualTo("regNo",regNo).whereEqualTo("date",date).limit(1);
                                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for(QueryDocumentSnapshot querySnapshot : queryDocumentSnapshots){
                                            temp = querySnapshot.toObject(GlucoReading.class);
                                            tempID = querySnapshot.getId();
                                        }
                                        Intent intent = new Intent(viewReading.this,enterReading.class);
                                        intent.putExtra("toUpdateGluco",temp);
                                        intent.putExtra("DocRef",tempID);
                                        intent.putExtra("activityName","viewReading");
                                        startActivity(intent);
                                        finish();
                                    }
                                });

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