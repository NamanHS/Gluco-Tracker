package tech.hans.glucotracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.media.MediaPlayer;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class enterReading extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MediaPlayer mp;

    String doctor_Email, family_Email,patientName;

    GlucoReading glucoReading;
    FirebaseUser firebaseUser;
    String regNo;

    String alert_email="", alert_password="";

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
            public void onClick(View view){
                DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(), "Calender");
            }
        });

        mp = MediaPlayer.create(this,getResources().getIdentifier("aknowledge", "raw", getPackageName()));

        enterReading = (Button) findViewById(R.id.enterReading);

        readingEntered = (EditText) findViewById(R.id.readingEntered);
        optionalNotes = (EditText) findViewById(R.id.optionalNotes);

        interval = (RadioGroup) findViewById(R.id.interval);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        regNo = firebaseUser.getUid().toString();

        //  JavaMailAPI javaMailAPI = new JavaMailAPI(this,"namansanura4zn@gmail.com","Alert","message gluco");
        //  javaMailAPI.execute();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://gluco-tracker-app-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("UserData");
        DatabaseReference myRef2 = database.getReference("Config");

            //  fetch doctor email id
            myRef.child(regNo).child("doc_email").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    doctor_Email = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            // fetching user / patient name
            myRef.child(regNo).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    patientName = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            //  fetch family email id
            myRef.child(regNo).child("fam_email").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    family_Email = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            //fetch email and password for sending email upon the out of range values
            //email fetching
            myRef2.child("Credentials").child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    alert_email = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            //password fetching
            myRef2.child("Credentials").child("password").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    alert_password = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });


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
            Toast.makeText(getApplicationContext(),"Please select INTERVAL\nBefore Meal or After Meal",Toast.LENGTH_SHORT).show();
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
                            mp.start();
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
            String alertmsg = "", message = "";
            boolean outOfRange = false;
            if (glucoReadingEntered >= 10 && glucoReadingEntered <= 39 && mealInterval.equals("Before Meal")) {
                alertmsg = "Very Low Glucose Level";
                message = patientName + " is tested with Glucose Level " + glucoReadingEntered + " mg/dL\nBefore Meal\nOn " + date;
                outOfRange = true;
            } else if (glucoReadingEntered >= 40 && glucoReadingEntered <= 69 && mealInterval.equals("Before Meal")) {
                alertmsg = "Low Glucose Level";
                message = patientName + " is tested with Glucose Level " + glucoReadingEntered + " mg/dL\nBefore Meal\nOn " + date;
                outOfRange = true;
            } else if (glucoReadingEntered >= 70 && glucoReadingEntered <= 130 && mealInterval.equals("Before Meal")) {
                outOfRange = false;
            } else if (glucoReadingEntered > 130 && glucoReadingEntered < 180 && mealInterval.equals("Before Meal")) {
                alertmsg = "High Glucose Level";
                message = patientName + " is tested with Glucose Level " + glucoReadingEntered + " mg/dL\nBefore Meal\nOn " + date;
                outOfRange = true;
            } else if (glucoReadingEntered >= 180 && mealInterval.equals("Before Meal")) {
                alertmsg = "Very High Glucose Level";
                message = patientName + " is tested with Glucose Level " + glucoReadingEntered + " mg/dL\nBefore Meal\nOn " + date;
                outOfRange = true;
            } else if (glucoReadingEntered <= 69 && mealInterval.equals("After Meal")) {
                alertmsg = "Very Low Glucose Level";
                message = patientName + " is tested with Glucose Level " + glucoReadingEntered + " mg/dL\nAfter Meal\nOn " + date;
                outOfRange = true;
            } else if (glucoReadingEntered >= 70 && glucoReadingEntered < 180 && mealInterval.equals("After Meal")) {
                outOfRange = false;
            } else if (glucoReadingEntered >= 180 && glucoReadingEntered <= 239 && mealInterval.equals("After Meal")) {
                alertmsg = "High Glucose Level";
                message = patientName + " is tested with Glucose Level " + glucoReadingEntered + " mg/dL\nAfter Meal\nOn " + date;
                outOfRange = true;
            } else if (glucoReadingEntered >= 240 && mealInterval.equals("After Meal")) {
                alertmsg = "Very High Glucose Reading";
                message = patientName + " is tested with Glucose Level " + glucoReadingEntered + " mg/dL\nAfter Meal\nOn " + date;
                outOfRange = true;
            }

            if (outOfRange) {
                    Log.i("checkpoint","3");
                    JavaMailAPI sendToDoctor = new JavaMailAPI(this, doctor_Email, alertmsg, message, alert_email, alert_password);
                    sendToDoctor.execute();
                    JavaMailAPI sendToFamily = new JavaMailAPI(this, family_Email, alertmsg, message, alert_email, alert_password);
                    sendToFamily.execute();
            }
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "YOU ARE NOT CONNECTED TO INTERNET", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }
}