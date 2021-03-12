package tech.hans.glucotracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public class SelfAssessment extends AppCompatActivity {

    public int count = 0;
    String tag = "";

    RadioGroup rg1, rg2, rg3, rg4, rg5, rg6, rg7, rg8, rg9, rg10, rg11, rg12, rg13;

    RequestQueue req;

    ProgressBar prog;

    String v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13;

    String output;
    JSONObject jsonObject;

    String url = "https://namanhs.pythonanywhere.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_assessment);

        req = Volley.newRequestQueue(this);

        prog = (ProgressBar) findViewById(R.id.progressBar);

        rg1 = findViewById(R.id.rg1);
        rg2 = findViewById(R.id.rg2);
        rg3 = findViewById(R.id.rg3);
        rg4 = findViewById(R.id.rg4);
        rg5 = findViewById(R.id.rg5);
        rg6 = findViewById(R.id.rg6);
        rg7 = findViewById(R.id.rg7);
        rg8 = findViewById(R.id.rg8);
        rg9 = findViewById(R.id.rg9);
        rg10 = findViewById(R.id.rg10);
        rg11 = findViewById(R.id.rg11);
        rg12 = findViewById(R.id.rg12);
        rg13 = findViewById(R.id.rg13);
    }

    public void onCheckResult(View view){
        if( rg1.getCheckedRadioButtonId() ==  -1  ||
            rg2.getCheckedRadioButtonId() ==  -1  ||
            rg3.getCheckedRadioButtonId() ==  -1  ||
            rg4.getCheckedRadioButtonId() ==  -1  ||
            rg5.getCheckedRadioButtonId() ==  -1  ||
            rg6.getCheckedRadioButtonId() ==  -1  ||
            rg7.getCheckedRadioButtonId() ==  -1  ||
            rg8.getCheckedRadioButtonId() ==  -1  ||
            rg9.getCheckedRadioButtonId() ==  -1  ||
            rg10.getCheckedRadioButtonId()  == -1 ||
            rg11.getCheckedRadioButtonId()  == -1 ||
            rg12.getCheckedRadioButtonId()  == -1 ||
            rg13.getCheckedRadioButtonId()  == -1)
        {
            String msg = "<Big><b><br><br><br><br><br><br><br><br><br><br>Please answer all the questions<br><br><br><br><br><br><br><br><br><br></b></Big>";
            Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml(msg), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }else{

            prog.setVisibility(View.VISIBLE);

            int b1 = rg1.getCheckedRadioButtonId();
            if(b1 == R.id.q1r1){
                v1 = "less than 40";
            }else if(b1 == R.id.q1r2){
                v1 = "40-49";
            }else if(b1 == R.id.q1r3){
                v1 = "50-59";
            }else{
                v1 = "60 or above";
                tag = "aged";
            }

            int b2 = rg2.getCheckedRadioButtonId();
            if(b2 == R.id.q2r1){
                v2 = "Male";
            }else{
                v2 = "Female";
            }

            int b3 = rg3.getCheckedRadioButtonId();
            if(b3 == R.id.q3r1){
                v3 = "Yes";
            }else{
                v3 = "No";
                count++;
            }

            int b4 = rg4.getCheckedRadioButtonId();
            if(b4 == R.id.q4r1){
                v4 = "Yes";
            }else{
                v4 = "No";
                count++;
            }

            int b5 = rg5.getCheckedRadioButtonId();
            if(b5 == R.id.q5r1){
                v5 = "Yes";
            }else{
                v5 = "No";
                count++;
            }

            int b6 = rg6.getCheckedRadioButtonId();
            if(b6 == R.id.q6r1){
                v6 = "Yes";
            }else{
                v6 = "No";
                count++;
            }

            int b7 = rg7.getCheckedRadioButtonId();
            if(b7 == R.id.q7r1){
                v7 = "Yes";
            }else{
                v7 = "No";
                count++;
            }

            int b8 = rg8.getCheckedRadioButtonId();
            if(b8 == R.id.q8r1){
                v8 = "Yes";
            }else{
                v8 = "No";
                count++;
            }

            int b9 = rg9.getCheckedRadioButtonId();
            if(b9 == R.id.q9r1){
                v9 = "Yes";
            }else{
                v9 = "No";
                count++;
            }

            int b10 = rg10.getCheckedRadioButtonId();
            if(b10 == R.id.q10r1){
                v10 = "Yes";
            }else{
                v10 = "No";
                count++;
            }

            int b11 = rg11.getCheckedRadioButtonId();
            if(b11 == R.id.q11r1){
                v11 = "Yes";
            }else{
                v11 = "No";
                count++;
            }

            int b12 = rg12.getCheckedRadioButtonId();
            if(b12 == R.id.q12r1){
                v12 = "Yes";
            }else{
                v12 = "No";
                count++;
            }

            int b13 = rg13.getCheckedRadioButtonId();
            if(b13 == R.id.q13r1){
                v13 = "Yes";
            }else{
                v13 = "No";
                count++;
            }

            LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
            map.put("Age",v1);
            map.put("Gender",v2);
            map.put("Polyuria",v3);
            map.put("Polydipsia",v4);
            map.put("sudden weight loss",v5);
            map.put("weakness",v6);
            map.put("Polyphagia",v7);
            map.put("visual blurring",v8);
            map.put("Itching",v9);
            map.put("Irritability",v10);
            map.put("delayed healing",v11);
            map.put("muscle stiffness",v12);
            map.put("Obesity",v13);

            try{
                jsonObject = new JSONObject(map);
            }catch (NullPointerException e){
                return;
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        output = response.getString("class");
                        Log.i("success--->",output);
                        Intent intent = new Intent(getApplicationContext(),Resulter.class);
                        intent.putExtra("output",output);
                        intent.putExtra("count",count);
                        intent.putExtra("tag",tag);
                        prog.setVisibility(View.GONE);
                        startActivity(intent);
                    } catch (JSONException e) {
                        String msg = "<Big><b><br><br><br><br><br><br><br><br><br><br>ERROR<br><br><br><br><br><br><br><br><br><br></b></Big>";
                        Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml(msg), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        prog.setVisibility(View.GONE);
                        toast.show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String msg = "<Big><b><br><br><br><br><br><br><br><br><br><br>PLEASE CHECK YOUR INTERNET CONNECTIVITY<br><br><br><br><br><br><br><br><br><br></b></Big>";
                    Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml(msg), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    prog.setVisibility(View.GONE);
                    toast.show();

                }
            });

            req.add(jsonObjectRequest);

        }
    }

    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finishAffinity();
    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 1);
    }


}