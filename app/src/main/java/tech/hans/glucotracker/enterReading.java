package tech.hans.glucotracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class enterReading extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    GlucoReading glucoReading;
    FirebaseUser firebaseUser;
    String regNo;

    TextView dateSelected;
    ImageView calendericon;
    String currentDate;
    Button enterReading;

    private EditText readingEntered, optionalNotes;
    private RadioGroup interval;

    int glucoReadingEntered;
    String mealInterval,mealOptionalNotes;
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_reading);
        dateSelected = (TextView) findViewById(R.id.dateSelected);
        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        date = calendar.getTime();
        dateSelected.setText(currentDate);
        calendericon = (ImageView) findViewById(R.id.calenderIcon);
        calendericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(), "Calender");
            }
        });

        enterReading = (Button) findViewById(R.id.enterReading);

        readingEntered = (EditText) findViewById(R.id.readingEntered);
        optionalNotes = (EditText) findViewById(R.id.optionalNotes);

        interval = (RadioGroup) findViewById(R.id.interval);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        regNo = firebaseUser.getUid().toString();

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, i);
        calendar.set(Calendar.MONTH, i1);
        calendar.set(Calendar.DAY_OF_MONTH, i2);
        currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        dateSelected.setText(currentDate);
        date = calendar.getTime();
    }


    public void enterReadingIntoApplication(View v) {
        //gluco reading
        if (readingEntered.getText().toString().isEmpty()) {
            readingEntered.setError("Please Enter Glucose Reading");
            return;
        } else if (Integer.parseInt(readingEntered.getText().toString()) < 10 || Integer.parseInt(readingEntered.getText().toString()) > 900) {
            readingEntered.setError("enter valid reading");
            return;
        }else{
            glucoReadingEntered = Integer.parseInt(readingEntered.getText().toString());
        }

        //meal interval
        if(interval.getCheckedRadioButtonId() == -1){
            Toast.makeText(getApplicationContext(),"Please select INTERVAL\n Before Meal or After Meal",Toast.LENGTH_SHORT).show();
            return;
        }else if(interval.getCheckedRadioButtonId() == R.id.beforeMeal){
            mealInterval = "Before Meal";

        }else{
            mealInterval = "After Meal";
        }

        //optional notes
        if(optionalNotes.getText().toString().isEmpty()){
            mealOptionalNotes = "No Notes Added";
            glucoReading = new GlucoReading(regNo,mealInterval,mealOptionalNotes,glucoReadingEntered,date);
        }else{
            mealOptionalNotes = optionalNotes.getText().toString();
            glucoReading = new GlucoReading(regNo,mealInterval,mealOptionalNotes,glucoReadingEntered,date);
        }
        addingToDataBase();
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



    public void addingToDataBase(){
        if(haveNetworkConnection()){
            db.collection("GlucoReadingDB").add(glucoReading)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast toast = Toast.makeText(getApplicationContext(), "SUCCESSFULLY ADDED GLUCOSE READING", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast toast = Toast.makeText(getApplicationContext(), "SERVER ERROR", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                    });
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "YOU ARE NOT CONNECTED TO INTERNET", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }


    }
}