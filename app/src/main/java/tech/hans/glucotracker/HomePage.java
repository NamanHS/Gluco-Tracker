package tech.hans.glucotracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomePage extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private CardView enterReading, viewReading, viewGraph, generateReport, meditateView, readArticles;

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.optionsmenu,menu);
        return  super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.logOutOfSystem:
                logOutOfSystem();
        }
        return true;
    }

    private void logOutOfSystem() {
        mAuth.signOut();
        startActivity(new Intent(this,MainActivity.class));
        finishAffinity();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //code for greeting user on home page
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance("https://gluco-tracker-app-default-rtdb.firebaseio.com/");
        myRef = database.getReference("UserData");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String regNo = firebaseUser.getUid();
        assert firebaseUser != null;
        myRef.child(regNo).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String greetname = dataSnapshot.getValue().toString();
                TextView greet = (TextView) findViewById(R.id.greet);
                greet.setText("Hey "+ greetname +"!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //--------------------------------------------code for greetings on home page ends

        //initializing CardViews
        enterReading = (CardView) findViewById(R.id.enterReading);
        viewReading = (CardView) findViewById(R.id.viewReading);
        viewGraph = (CardView) findViewById(R.id.viewGraph);
        generateReport = (CardView) findViewById(R.id.generateReport);
        meditateView = (CardView) findViewById(R.id.meditateView);
        readArticles = (CardView) findViewById(R.id.readArticles);

        //set on click listener on card views

        enterReading.setOnClickListener(this);
        viewReading.setOnClickListener(this);
        viewGraph.setOnClickListener(this);
        generateReport.setOnClickListener(this);
        meditateView.setOnClickListener(this);
        readArticles.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.enterReading:
                startActivity(new Intent(this,enterReading.class));
                break;
            case R.id.viewReading:
                startActivity(new Intent(this,viewReading.class));
                break;
            case R.id.viewGraph:
                startActivity(new Intent(this,viewGraph.class));
                break;
            case R.id.generateReport:
                startActivity(new Intent(this,generateReport.class));
                break;
            case R.id.meditateView:
                startActivity(new Intent(this,meditateView.class));
                break;
            case R.id.readArticles:
                startActivity(new Intent(this,readArticles.class));
                break;
        }
    }
}