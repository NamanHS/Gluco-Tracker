package tech.hans.glucotracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener , DatePickerDialog.OnDateSetListener {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ImageView calendericon;

    String currentDate;
    Date date;
    Long val;


    Button signUp;
    TextView logIn;

    EditText name, email, docEmail, famEmail, passw, cnfpassw,dob;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        mAuth = FirebaseAuth.getInstance();


        signUp = (Button) findViewById(R.id.signUp);
        logIn = (TextView) findViewById(R.id.logIn);

        signUp.setOnClickListener(this);
        logIn.setOnClickListener(this);

        name = (EditText) findViewById(R.id.name);
        dob = (EditText) findViewById(R.id.dob);
        email = (EditText) findViewById(R.id.email);
        docEmail = (EditText) findViewById(R.id.docEmail);
        famEmail = (EditText) findViewById(R.id.famEmail);
        passw = (EditText) findViewById(R.id.passw);
        cnfpassw = (EditText) findViewById(R.id.cnfpassw);
        loading = (ProgressBar) findViewById(R.id.loading);

        calendericon = (ImageView) findViewById(R.id.calenderIcon2);
        calendericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(), "Calender");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signUp:
                signUpUser();
                break;
            case R.id.logIn:
                startActivity(new Intent(this, LogInUser.class));
                break;
            default:
                break;
        }
    }

    private void signUpUser() {

        final String fname = name.getText().toString().trim();
        final String femail = email.getText().toString().trim();
        final String fdocEmail = docEmail.getText().toString().trim();
        final String ffamEmail = famEmail.getText().toString().trim();
        String fpassw = passw.getText().toString();
        String fcnfpassw = cnfpassw.getText().toString();



        if (fname.isEmpty()) {
            name.setError("Name is required");
            name.requestFocus();
            return;
        }
        if(dob.getText().toString().equals("")){
            Toast toast = Toast.makeText(getApplicationContext(), "\n\nPLEASE SELECT YOUR DATE OF BIRTH\n\n", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        if (fname.length() <= 1) {
            name.setError("name is too short, please enter name of at least 2 characters");
            name.requestFocus();
            return;
        }
        if (femail.isEmpty()) {
            email.setError("Email id is required");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(femail).matches()) {
            email.setError("Invalid Email Id");
            email.requestFocus();
            return;
        }
        if (fdocEmail.isEmpty()) {
            docEmail.setError("Doctor's Email id is required");
            docEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(fdocEmail).matches()) {
            docEmail.setError("Invalid Email Id");
            docEmail.requestFocus();
            return;
        }
        if (ffamEmail.isEmpty()) {
            famEmail.setError("family member's Email id i required");
            famEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(ffamEmail).matches()) {
            famEmail.setError("Invalid Email Id");
            famEmail.requestFocus();
            return;
        }
        if (fpassw.isEmpty()) {
            passw.setError("setting a password is required");
            passw.requestFocus();
            return;
        }

        //fpassw.length() < 6
        if (!isValidPassword(fpassw)) {
            passw.setError("Password must contain atleast :\none digit [0-9]\none lowercase character [a-z]\none uppercase character [A-Z]\none special character like ! @ # & ( ).\n\n\nPassword must contain a length of at least 6 characters\n");
            passw.requestFocus();
            return;
        }
        if (fcnfpassw.isEmpty()) {
            cnfpassw.setError("Re-enter password");
            cnfpassw.requestFocus();
            return;
        }
        if (!fpassw.equals(fcnfpassw)) {
            cnfpassw.setError("Password does not match");
            cnfpassw.requestFocus();
            return;
        }

        if(haveNetworkConnection()){
            loading.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(femail, fpassw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @SuppressLint("ShowToast")
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task){
                            if (task.isSuccessful()) {
                                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                assert firebaseUser != null;
                                String regNo = firebaseUser.getUid();
                                UserData userData = new UserData(fname,val,femail,fdocEmail,ffamEmail);
                                FirebaseDatabase database = FirebaseDatabase.getInstance("https://gluco-tracker-app-default-rtdb.firebaseio.com/");
                                DatabaseReference myRef = database.getReference("UserData");

                                myRef.child(regNo).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            firebaseUser.sendEmailVerification();
                                            loading.setVisibility(View.GONE);
                                            startActivity(new Intent(getApplicationContext(),LogInUser.class));
                                            String msg = "<Big><b><br><br><br><br><br><br><br><br><br><br>VERIFICATION MAIL HAS BEEN SENT TO EMAIL ID:<br>"+ femail+"<br><br>PLEASE VERIFY YOUR MAIL BEFORE LOG IN <br><br><br><br><br><br><br><br><br></b></Big>";
                                            Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml(msg), Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                        }else{
                                            loading.setVisibility(View.GONE);
                                            String msg = "<Big><b><br><br><br><br><br><br><br><br><br><br>REGISTRATION FAILED<br><br><br><br><br><br><br><br><br><br></b></Big>";
                                            Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml(msg), Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                        }
                                    }
                                });

                            } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                loading.setVisibility(View.GONE);
                                String msg = "<Big><b><br><br><br><br><br><br><br><br><br><br>A user with Email id<br>" + femail + "<br>is already registered<br><br><br><br><br><br><br><br><br><br></b></Big>";
                                Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml(msg), Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }

                        }
                    });
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "YOU ARE NOT CONNECTED TO INTERNET\n\nCONNECT TO INTERNET TO USE APPLICATION", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }



    }


    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{6,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, i);
        calendar.set(Calendar.MONTH, i1);
        calendar.set(Calendar.DAY_OF_MONTH, i2);

        date = calendar.getTime();
        val = calendar.getTimeInMillis();

        currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        dob.setText(currentDate);
        //date = calendar.getTime();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

}