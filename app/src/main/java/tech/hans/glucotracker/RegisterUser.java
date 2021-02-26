package tech.hans.glucotracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();;

    Button signUp;
    TextView logIn;

    EditText name, email, docEmail, famEmail, passw, cnfpassw;
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
        email = (EditText) findViewById(R.id.email);
        docEmail = (EditText) findViewById(R.id.docEmail);
        famEmail = (EditText) findViewById(R.id.famEmail);
        passw = (EditText) findViewById(R.id.passw);
        cnfpassw = (EditText) findViewById(R.id.cnfpassw);
        loading = (ProgressBar) findViewById(R.id.loading);
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
            passw.setError("setting password is required");
            passw.requestFocus();
            return;
        }
        if (fpassw.length() < 6) {
            passw.setError("password must be at least 6 characters long");
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

        loading.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(femail, fpassw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            assert firebaseUser != null;

                            String regNo = firebaseUser.getUid();
                            UserData userData = new UserData(fname,femail,fdocEmail,ffamEmail);
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
                        } else {
                            loading.setVisibility(View.GONE);
                            String msg = "<Big><b><br><br><br><br><br><br><br><br><br><br>PLEASE CHECK YOUR INTERNET CONNECTIVITY<br><br><br><br><br><br><br><br><br><br></b></Big>";
                            Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml(msg), Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });


        //finishAffinity();
        //startActivity(new Intent(this,HomePage.class));

    }


}