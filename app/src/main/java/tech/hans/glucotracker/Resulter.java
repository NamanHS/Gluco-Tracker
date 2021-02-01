package tech.hans.glucotracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Resulter extends AppCompatActivity {




    TextView outputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resulter);

        outputText = (TextView) findViewById(R.id.outputText);
        outputText.setVisibility(View.GONE);
        Intent intent = getIntent();
        String output = intent.getStringExtra("output");

        if(output.equals("Positive")){
            outputText.setText("BASED ON YOUR CURRENT SYMPTOMS,\n\nYOU ARE AT RISK OF DEVELOPING DIABETES\n\nPLEASE CONSULT A DOCTOR AND GET YOURSELF A CHECK UP FOR DIABETES");
            outputText.setVisibility(View.VISIBLE);
        }else if(output.equals("Negative")){
            outputText.setText("BASED ON YOUR CURRENT SYMPTOMS,\n\nYOU ARE NOT AT A RISK OF DEVELOPING DIABETES\n\nHOWEVER, HEALTHY LIFESTYLE SHOULD BE FOLLOWED TO KEEP DIABETES AT A BAY");
            outputText.setVisibility(View.VISIBLE);
        }



    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 1);
    }

    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finishAffinity();
    }


}