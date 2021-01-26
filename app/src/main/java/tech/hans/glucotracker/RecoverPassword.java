package tech.hans.glucotracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class RecoverPassword extends AppCompatActivity {

    EditText recoveryEmail;
    Button resetPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        mAuth = FirebaseAuth.getInstance();

        recoveryEmail = (EditText) findViewById(R.id.recoveryEmail);
        resetPassword = (Button) findViewById(R.id.resetPasswordButton);

    }

    public void passwordRecovery(View view){
        final String frecoveryEmail = recoveryEmail.getText().toString();

        if(frecoveryEmail.isEmpty()){
            recoveryEmail.setError("Email id is required");
            recoveryEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(frecoveryEmail).matches()){
            recoveryEmail.setError("Invalid Email Id");
            recoveryEmail.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(frecoveryEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    String msg = "<Big><b><br><br><br><br><br><br><br><br><br><br>EMAIL HAS BEEN SENT TO YOU TO RESET PASSWORD ON YOUR EMAIL ID<br>"+frecoveryEmail+"<br><br><br><br><br><br><br><br><br></b></Big>";
                    Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml(msg), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    finish();
                }else if(task.getException() instanceof FirebaseAuthInvalidUserException){
                    String msg = "<Big><b><br><br><br><br><br><br><br><br><br><br>YOU DO NOT HAVE ACCOUNT WITH EMAIL ID:<br>"+frecoveryEmail+"<br><br><br><br><br><br><br><br><br></b></Big>";
                    Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml(msg), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else{
                    String msg = "<Big><b><br><br><br><br><br><br><br><br><br><br>PLEASE CHECK YOUR INTERNET CONNECTIVITY<br><br><br><br><br><br><br><br><br><br></b></Big>";
                    Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml(msg), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

    }
}