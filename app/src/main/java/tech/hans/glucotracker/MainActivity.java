package tech.hans.glucotracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button sat, gin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        //No title and Action Bar
        setTitle(null);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        setContentView(R.layout.activity_main);

        //buttons
        sat = (Button) findViewById(R.id.SAT);
        gin = (Button) findViewById(R.id.getInside);

        sat.setOnClickListener(this);
        gin.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.SAT:
                getIntoApp("SAT");
                break;
            case R.id.getInside:
                getIntoApp("getInside");

                break;
            default:
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

        public void getIntoApp(String actName){
         if(haveNetworkConnection()){
             if(actName.equals("SAT")){
                 startActivity(new Intent(this,SelfAssessment.class));
             }else if(actName.equals("getInside")){
                 if(mAuth.getCurrentUser()==null) {
                     startActivity(new Intent(this, RegisterUser.class));
                 }else{
                     startActivity(new Intent(this,HomePage.class));
                 }
             }
         }else{
             Toast toast = Toast.makeText(getApplicationContext(), "YOU ARE NOT CONNECTED TO INTERNET\n\nCONNECT TO INTERNET TO USE APPLICATION", Toast.LENGTH_LONG);
             toast.setGravity(Gravity.CENTER,0,0);
             toast.show();
         }
        }

    }
