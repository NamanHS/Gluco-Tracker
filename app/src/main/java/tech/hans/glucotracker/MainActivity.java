package tech.hans.glucotracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
                startActivity(new Intent(this,SelfAssessment.class));
                break;
            case R.id.getInside:
                if(mAuth.getCurrentUser()==null) {
                    startActivity(new Intent(this, RegisterUser.class));
                }else{
                    startActivity(new Intent(this,HomePage.class));
                }
                break;
            default:
                break;
        }
        }

    }
