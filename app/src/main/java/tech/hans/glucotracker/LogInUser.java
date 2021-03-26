package tech.hans.glucotracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LogInUser extends AppCompatActivity implements View.OnClickListener {
    
    private Button login;
    private ProgressBar loginLoading;
    private TextView forgotPassword, signup;
    private EditText loginEmail, loginPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_user);


        mAuth = FirebaseAuth.getInstance();

        loginLoading = (ProgressBar) findViewById(R.id.loginLoading);
        login = (Button) findViewById(R.id.logIntoGetIn);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        signup = (TextView) findViewById(R.id.singUptoRegister);

        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);

        login.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.logIntoGetIn:
                logInUser();
                return;
            case R.id.forgotPassword:
                startActivity(new Intent(getApplicationContext(),RecoverPassword.class));
                return;
            case R.id.singUptoRegister:
                finish();
                return;
            default:
        }

    }

    private void logInUser() {

        final String floginEmail = loginEmail.getText().toString().trim();
        String floginPassword = loginPassword.getText().toString();

        if(floginEmail.isEmpty()){
            loginEmail.setError("Email id is required");
            loginEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(floginEmail).matches()){
            loginEmail.setError("Invalid Email Id");
            loginEmail.requestFocus();
            return;
        }

        if(floginPassword.isEmpty()){
            loginPassword.setError("Password is required");
            loginPassword.requestFocus();
            return;
        }

        if(floginPassword.length() < 6){
            loginPassword.setError("Invalid Password");
            loginPassword.requestFocus();
            return;
        }


           loginLoading.setVisibility(View.VISIBLE);
           mAuth.signInWithEmailAndPassword(floginEmail,floginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        if(mAuth.getCurrentUser().isEmailVerified()){
                            loginLoading.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(),HomePage.class));
                            finishAffinity();
                        }else{
                            loginLoading.setVisibility(View.GONE);
                            String msg = "<Big><b><br><br><br><br><br><br><br><br><br><br>PLEASE VERIFY YOUR EMAIL ID<br><br>EMAIL HAS BEEN SENT TO YOUR EMAIL ID FOR VERIFICATION<br><br><br><br><br><br><br></b></Big>";
                            Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml(msg), Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }else{
                        if(task.getException() instanceof FirebaseAuthInvalidUserException){
                            loginLoading.setVisibility(View.GONE);
                            String msg = "<Big><b><br><br><br><br><br><br><br><br><br><br>You dont have an account with Email Id:<br>"+floginEmail+"<br>Please Sign up first<br><br><br><br><br><br><br></b></Big>";
                            Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml(msg), Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            loginLoading.setVisibility(View.GONE);
                            String msg = "<Big><b><br><br><br><br><br><br><br><br><br><br>INVALID PASSWORD<br><br><br><br><br><br><br></b></Big>";
                            Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml(msg), Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        else{
                            loginLoading.setVisibility(View.GONE);
                            String msg = "<Big><b><br><br><br><br><br><br><br><br><br><br>PLEASE CHECK YOUR INTERNET CONNECTIVITY<br><br><br><br><br><br><br><br><br><br></b></Big>";
                            Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml(msg), Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    }
               }
           });



        //finishAffinity();
        //startActivity(new Intent(this,HomePage.class));
    }

}