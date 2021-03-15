package tech.hans.glucotracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
        if(!haveNetworkConnection()) {
            Toast toast = Toast.makeText(getApplicationContext(), "YOU ARE NOT CONNECTED TO INTERNET\nCONNECT TO INTERNET TO USE APPLICATION", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

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
                enterActivity("enterReading");
                break;
            case R.id.viewReading:
                enterActivity("viewReading");
                break;
            case R.id.viewGraph:
                enterActivity("viewGraph");
                break;
            case R.id.generateReport:
                enterActivity("generateReport");
                break;
            case R.id.meditateView:
                enterActivity("meditateView");
                break;
            case R.id.readArticles:
                enterActivity("readArticles");
                break;
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public void enterActivity(String activityName){
        if(haveNetworkConnection()){
            if(activityName.equals("enterReading")){
                Intent intent = new Intent(HomePage.this,enterReading.class);
                intent.putExtra("activityName","HomePage");
                startActivity(new Intent(this,enterReading.class));
            }else if(activityName.equals("viewReading")){
                startActivity(new Intent(this,viewReading.class));
            }else if(activityName.equals("viewGraph")){
                startActivity(new Intent(this,viewGraph.class));
            }else if(activityName.equals("generateReport")){
                startActivity(new Intent(this,generateReport.class));
            }else if(activityName.equals("meditateView")){
                startActivity(new Intent(this,meditateView.class));
            }else if(activityName.equals("readArticles")){
                startActivity(new Intent(this,readArticles.class));
            }

        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "YOU ARE NOT CONNECTED TO INTERNET\n\nCONNECT TO INTERNET TO USE APPLICATION", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }





}