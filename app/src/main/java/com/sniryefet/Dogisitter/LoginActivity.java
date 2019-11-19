package com.sniryefet.Dogisitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmailView = findViewById(R.id.login_email);
        mPasswordView = findViewById(R.id.login_password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mAuth=FirebaseAuth.getInstance();

    }

    // Executed when Sign in button pressed
    //listener from the xml file
    public void signInExistingUser(View v)   {
        attemptLogin();
    }

    // Executed when Register button pressed
    //listener from the xml file
    public void registerNewUser(View v) {
        Intent intent = new Intent(this, com.sniryefet.Dogisitter.RegisterActivity.class);
        finish();
        startActivity(intent);
    }

    private void attemptLogin() {

        String password = mPasswordView.getText().toString();
        String email = mEmailView.getText().toString();

        //one of the field is blank
        if(password.length()<1 || email.length()<1) return;

        Toast.makeText(this,"Login In Progress...",Toast.LENGTH_SHORT).show();


        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(!task.isSuccessful()){
                        Log.d("DogisitterApp","Login Failed "+task.getException());
                        showErrorDialog("There was an error signing in");
                    }else{
                         Log.d("DogisitterApp","Login was successful");
                        //go to the profile page of the admin/client
                        Intent intent= new Intent(LoginActivity.this,AdminMainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                }
            });


    }

    private void showErrorDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}
